package com.xiilab.modulek8s.facade.workload;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.node.dto.GpuInfoDTO;
import com.xiilab.modulek8s.node.service.NodeService;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateDistributedWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCredentialReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDistributedJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.log.service.LogService;
import com.xiilab.modulek8s.workload.secret.service.SecretService;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.enums.SvcType;
import com.xiilab.modulek8s.workload.svc.service.SvcService;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadModuleFacadeServiceImpl implements WorkloadModuleFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final WorkspaceService workspaceService;
	private final VolumeService volumeService;
	private final SvcService svcService;
	private final LogService logService;
	private final SecretService secretService;
	private final NodeService nodeService;

	@Override
	public CreateJobResDTO createJobWorkload(CreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		//생성 요청한 workspace가 존재하는지 확인
		WorkspaceDTO.ResponseDTO workspaceByName = workspaceService.getWorkspaceByName(
			moduleCreateWorkloadReqDTO.getWorkspace());

		WorkloadType workloadType = moduleCreateWorkloadReqDTO.getWorkloadType();
		// Secret 생성
		if (moduleCreateWorkloadReqDTO.getImage().getRepositoryAuthType() == RepositoryAuthType.PRIVATE) {
			createAndSetImageSecret(moduleCreateWorkloadReqDTO);
		}

		CreateJobResDTO createJobResDTO = null;
		try {
			// Dataset PV 생성
			createPVAndPVC(moduleCreateWorkloadReqDTO.getDatasets());
			// Model PV 생성
			createPVAndPVC(moduleCreateWorkloadReqDTO.getModels());
			if (workloadType == WorkloadType.BATCH) {
				createJobResDTO = workloadModuleService.createBatchJobWorkload(
					(ModuleCreateWorkloadReqDTO)moduleCreateWorkloadReqDTO,
					workspaceByName.getName());
			} else if (workloadType == WorkloadType.INTERACTIVE) {
				createJobResDTO = workloadModuleService.createInteractiveJobWorkload(
					(ModuleCreateWorkloadReqDTO)moduleCreateWorkloadReqDTO,
					workspaceByName.getName());
			} else if (workloadType == WorkloadType.DISTRIBUTED) {
				return workloadModuleService.createDistributedJobWorkload(
					(ModuleCreateDistributedWorkloadReqDTO)moduleCreateWorkloadReqDTO,
					workspaceByName.getName());
			}

			if (!CollectionUtils.isEmpty(moduleCreateWorkloadReqDTO.getPorts())) {
				CreateSvcReqDTO createSvcReqDTO = CreateSvcReqDTO.createWorkloadReqDTOToCreateServiceDto(
					moduleCreateWorkloadReqDTO, createJobResDTO.getName(), createJobResDTO.getResourceName());

				// 노드포트 연결
				svcService.createNodePortService(createSvcReqDTO);
			}
		} catch (Exception e) {
			// log.error(e.getMessage());
			e.printStackTrace();
			// Dataset PV 삭제
			if (!ObjectUtils.isEmpty(moduleCreateWorkloadReqDTO.getDatasets())) {
				for (ModuleVolumeReqDTO dataset : moduleCreateWorkloadReqDTO.getDatasets()) {
					volumeService.deletePVC(dataset.getCreatePV().getPvcName(), dataset.getCreatePV().getNamespace());
					volumeService.deletePV(dataset.getCreatePV().getPvName());
				}
			}
			// Model PV 삭제
			if (!ObjectUtils.isEmpty(moduleCreateWorkloadReqDTO.getModels())) {
				for (ModuleVolumeReqDTO model : moduleCreateWorkloadReqDTO.getModels()) {
					volumeService.deletePVC(model.getCreatePV().getPvcName(), model.getCreatePV().getNamespace());
					volumeService.deletePV(model.getCreatePV().getPvName());
				}
			}

			throw new RestApiException(WorkloadErrorCode.FAILED_CREATE_WORKLOAD);
		}

		return createJobResDTO;
	}

	private void createPVAndPVC(List<ModuleVolumeReqDTO> list) {
		if (!CollectionUtils.isEmpty(list)) {
			for (ModuleVolumeReqDTO reqDto : list) {
				volumeService.createPV(reqDto.getCreatePV());
				volumeService.createPVC(reqDto.getCreatePVC());
			}
		}
	}

	private void createAndSetImageSecret(CreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		ModuleCredentialReqDTO credentialReqDTO = moduleCreateWorkloadReqDTO.getImage().getCredentialReqDTO();
		String imageSecretName = null;
		if (credentialReqDTO != null && credentialReqDTO.credentialId() != null
			&& credentialReqDTO.credentialId() > 0) {
			imageSecretName = secretService.createSecret(moduleCreateWorkloadReqDTO);
			if (StringUtils.hasText(imageSecretName)) {
				moduleCreateWorkloadReqDTO.setImageSecretName(imageSecretName);
			}
		} else {
			throw new RuntimeException("이미지 Credential 정보가 올바르지 않습니다.");
		}
	}

	@Override
	public ModuleBatchJobResDTO getBatchWorkload(String workSpaceName, String workloadName) {
		return workloadModuleService.getBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveWorkload(String workSpaceName, String workloadName) {
		return workloadModuleService.getInteractiveJobWorkload(
			workSpaceName, workloadName);
	}

	@Override
	public ModuleDistributedJobResDTO getDistributedWorkload(String workspaceName, String workloadResourceName) {
		return workloadModuleService.getDistributedJobWorkload(workspaceName, workloadResourceName);
	}

	@Override
	public void deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteBatchJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public void deleteDistributedWorkload(String workspaceName, String workloadName) {
		workloadModuleService.deleteDistributedWorkload(workspaceName, workloadName);
	}

	@Override
	public List<AbstractModuleWorkloadResDTO> getWorkloadList(String workSpaceName) {
		List<AbstractModuleWorkloadResDTO> workloadList = new ArrayList<>();
		List<ModuleBatchJobResDTO> jobWorkloadList = workloadModuleService.getBatchWorkloadListByCondition(
			workSpaceName, null, null);
		List<ModuleInteractiveJobResDTO> workloadResList = workloadModuleService.getInteractiveWorkloadListByCondition(
			workSpaceName, null, null);
		if (!jobWorkloadList.isEmpty()) {
			workloadList.addAll(jobWorkloadList);
		}
		if (!workloadResList.isEmpty()) {
			workloadList.addAll(workloadResList);
		}

		return workloadList;
	}

	@Override
	public LogWatch watchLogByWorkload(String workspaceName, String podName) {
		return logService.watchLogByWorkload(workspaceName, podName);
	}

	public AbstractModuleWorkloadResDTO getUserRecentlyWorkload(String workspaceName, String username) {
		List<AbstractModuleWorkloadResDTO> workloadList = getWorkloadList(workspaceName);
		try {
			return workloadList.stream()
				.filter(workload -> workload.getCreatorUserName().equals(username))
				.sorted(Comparator.comparing(AbstractModuleWorkloadResDTO::getCreatedAt).reversed())
				.toList()
				.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getWorkloadLogByWorkloadName(String workspace, String workload, WorkloadType type) {
		Pod pod = getJobPod(workspace, workload, type);
		String namespace = pod.getMetadata().getNamespace();
		String podName = pod.getMetadata().getName();
		return logService.getWorkloadLogByWorkloadName(namespace, podName);
	}

	@Override
	public CreateLocalDatasetResDTO createLocalDataset(CreateLocalDatasetDTO createLocalDatasetDTO) {
		String datasetName = createLocalDatasetDTO.getDatasetName().replace(" ", "");
		String ip = createLocalDatasetDTO.getIp();
		String storagePath = createLocalDatasetDTO.getStoragePath();
		String namespace = createLocalDatasetDTO.getNamespace();
		String pvcName = "astrago-dataset-pvc-" + UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-dataset-pv-" + UUID.randomUUID().toString().substring(6);
		String svcName = "astrago-dataset-svc-" + UUID.randomUUID().toString().substring(6);
		String datasetDeploymentName = "astrago-dataset-" + UUID.randomUUID().toString().substring(6);
		String volumeLabelSelectorName = "dataset-storage-volume-" + UUID.randomUUID().toString().substring(6);
		String connectTestLabelName = "dataset-connect-test-" + UUID.randomUUID().toString().substring(6);

		//pv 생성
		CreatePV createPV = CreatePV.builder()
			.pvName(pvName)
			.pvcName(pvcName)
			.ip(ip)
			.storagePath(storagePath)
			.storageType(StorageType.NFS)
			.requestVolume(50)
			.namespace(namespace)
			.build();
		volumeService.createPV(createPV);
		//pvc 생성
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(namespace)
			.requestVolume(50)
			.build();
		volumeService.createPVC(createPVC);
		//deployment 생성
		CreateDatasetDeployment createDeployment = CreateDatasetDeployment.builder()
			.datasetName(datasetName)
			.deploymentName(datasetDeploymentName)
			.volumeLabelSelectorName(volumeLabelSelectorName)
			.pvcName(pvcName)
			.pvName(pvName)
			.namespace(namespace)
			.connectTestLabelName(connectTestLabelName)
			.hostPath(createLocalDatasetDTO.getHostPath())
			.dockerImage(createLocalDatasetDTO.getDockerImage())
			.build();
		workloadModuleService.createDatasetDeployment(createDeployment);
		//svc 생성
		//svc -> labels -> app: connectTestLabelName
		//connectTestLabelName, namespace, svcName, ClusterIP
		CreateClusterIPSvcReqDTO createClusterIPSvcReqDTO = CreateClusterIPSvcReqDTO.builder()
			.svcType(SvcType.CLUSTER_IP)
			.deploymentName(connectTestLabelName)
			.svcName(svcName)
			.namespace(namespace)
			.build();
		svcService.createClusterIPService(createClusterIPSvcReqDTO);

		//<service-name>.<namespace>.svc.cluster.local
		String svcDNS = svcName + "." + namespace + ".svc.cluster.local/directory";
		return CreateLocalDatasetResDTO.builder()
			.dns(svcDNS)
			.deploymentName(datasetDeploymentName)
			.pvcName(pvcName)
			.pvName(pvName)
			.svcName(svcName)
			.build();
	}

	@Override
	public WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id) {
		return workloadModuleService.workloadsUsingDataset(pageNo, pageSize, id);
	}

	@Override
	public void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO) {
		workloadModuleService.modifyLocalDatasetDeployment(modifyLocalDatasetDeploymentDTO);
	}

	@Override
	public boolean isUsedDataset(Long datasetId) {
		return workloadModuleService.isUsedDataset(datasetId);
	}

	@Override
	public void deleteLocalDataset(DeleteLocalDatasetDTO deleteLocalDatasetDTO) {
		String deploymentName = deleteLocalDatasetDTO.getDeploymentName();
		String svcName = deleteLocalDatasetDTO.getSvcName();
		String pvcName = deleteLocalDatasetDTO.getPvcName();
		String pvName = deleteLocalDatasetDTO.getPvName();
		String namespace = deleteLocalDatasetDTO.getNamespace();
		//svc 삭제
		svcService.deleteServiceByResourceName(svcName, namespace);
		//deployment 삭제
		workloadModuleService.deleteDeploymentByResourceName(deploymentName, namespace);
		//pvc 삭제
		volumeService.deletePVC(pvcName, namespace);
		//pv 삭제
		volumeService.deletePV(pvName);
	}

	@Override
	public CreateLocalModelResDTO createLocalModel(CreateLocalModelDTO createDto) {
		String modelName = createDto.getModelName().replace(" ", "");
		String ip = createDto.getIp();
		String storagePath = createDto.getStoragePath();
		String namespace = createDto.getNamespace();
		String pvcName = "astrago-model-pvc-" + UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-model-pv-" + UUID.randomUUID().toString().substring(6);
		String svcName = "astrago-model-svc-" + UUID.randomUUID().toString().substring(6);
		String modelDeploymentName = "astrago-model-" + UUID.randomUUID().toString().substring(6);
		String volumeLabelSelectorName = "model-storage-volume-" + UUID.randomUUID().toString().substring(6);
		String connectTestLabelName = "model-connect-test-" + UUID.randomUUID().toString().substring(6);

		//pv 생성
		CreatePV createPV = CreatePV.builder()
			.pvName(pvName)
			.pvcName(pvcName)
			.ip(ip)
			.storagePath(storagePath)
			.storageType(StorageType.NFS)
			.requestVolume(50)
			.namespace(namespace)
			.build();
		volumeService.createPV(createPV);
		//pvc 생성
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(namespace)
			.requestVolume(50)
			.build();
		volumeService.createPVC(createPVC);
		//deployment 생성
		CreateModelDeployment createDeployment = CreateModelDeployment.builder()
			.modelName(modelName)
			.deploymentName(modelDeploymentName)
			.volumeLabelSelectorName(volumeLabelSelectorName)
			.pvcName(pvcName)
			.pvName(pvName)
			.namespace(namespace)
			.connectTestLabelName(connectTestLabelName)
			.hostPath(createDto.getHostPath())
			.dockerImage(createDto.getDockerImage())
			.build();
		workloadModuleService.createModelDeployment(createDeployment);
		//svc 생성
		//svc -> labels -> app: connectTestLabelName
		//connectTestLabelName, namespace, svcName, ClusterIP
		CreateClusterIPSvcReqDTO createClusterIPSvcReqDTO = CreateClusterIPSvcReqDTO.builder()
			.svcType(SvcType.CLUSTER_IP)
			.deploymentName(connectTestLabelName)
			.svcName(svcName)
			.namespace(namespace)
			.build();
		svcService.createClusterIPService(createClusterIPSvcReqDTO);

		//<service-name>.<namespace>.svc.cluster.local
		String svcDNS = svcName + "." + namespace + ".svc.cluster.local/directory";
		return CreateLocalModelResDTO.builder()
			.dns(svcDNS)
			.deploymentName(modelDeploymentName)
			.pvcName(pvcName)
			.pvName(pvName)
			.svcName(svcName)
			.build();
	}

	@Override
	public WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id) {
		return workloadModuleService.workloadsUsingModel(pageNo, pageSize, id);
	}

	@Override
	public void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalDatasetDeploymentDTO) {
		workloadModuleService.modifyLocalModelDeployment(modifyLocalDatasetDeploymentDTO);
	}

	@Override
	public boolean isUsedModel(Long modelId) {
		return workloadModuleService.isUsedModel(modelId);
	}

	@Override
	public void deleteLocalModel(DeleteLocalModelDTO deleteLocalModelDTO) {
		String deploymentName = deleteLocalModelDTO.getDeploymentName();
		String svcName = deleteLocalModelDTO.getSvcName();
		String pvcName = deleteLocalModelDTO.getPvcName();
		String pvName = deleteLocalModelDTO.getPvName();
		String namespace = deleteLocalModelDTO.getNamespace();
		//svc 삭제
		svcService.deleteServiceByResourceName(svcName, namespace);
		//deployment 삭제
		workloadModuleService.deleteDeploymentByResourceName(deploymentName, namespace);
		//pvc 삭제
		volumeService.deletePVC(pvcName, namespace);
		//pv 삭제
		volumeService.deletePV(pvName);
	}

	@Override
	public void editBatchJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		workloadModuleService.editBatchJob(workspaceResourceName, workloadResourceName, name, description);
	}

	@Override
	public void editInteractiveJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		workloadModuleService.editInteractiveJob(workspaceResourceName, workloadResourceName, name, description);
	}

	@Override
	public List<Pod> getWorkloadByWorkloadName(String resourceName) {
		return workloadModuleService.getWorkloadByWorkloadName(resourceName);
	}

	@Override
	public GpuInfoDTO getGpuInfoByNodeName(String gpuName, String nodeName) {
		//node를 통해 gpu 정보 조회
		return nodeService.getGpuInfoByNodeName(gpuName, nodeName);
	}

	@Override
	public Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType) {
		return workloadModuleService.getJobPod(workspaceName, workloadName, workloadType);
	}

}
