package com.xiilab.modulek8s.facade.workload;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xiilab.modulek8s.common.enumeration.RepositoryAuthType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.common.enumeration.VolumeSelectionType;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCredentialReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.log.service.LogService;
import com.xiilab.modulek8s.workload.secret.service.SecretService;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.enums.SvcType;
import com.xiilab.modulek8s.workload.svc.service.SvcService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadModuleFacadeServiceImpl implements WorkloadModuleFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final VolumeService volumeService;
	private final SvcService svcService;
	private final LogService logService;
	private final SecretService secretService;

	@Override
	public ModuleJobResDTO createJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		WorkloadType workloadType = moduleCreateWorkloadReqDTO.getWorkloadType();
		// Secret 생성
		if (moduleCreateWorkloadReqDTO.getImage().repositoryAuthType() == RepositoryAuthType.PRIVATE) {
			createAndSetImageSecret(moduleCreateWorkloadReqDTO);
		}

		ModuleJobResDTO moduleJobResDTO = null;
		if (workloadType == WorkloadType.BATCH) {
			moduleJobResDTO = workloadModuleService.createBatchJobWorkload(moduleCreateWorkloadReqDTO);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			moduleJobResDTO = workloadModuleService.createInteractiveJobWorkload(moduleCreateWorkloadReqDTO);
		}

		CreateSvcReqDTO createSvcReqDTO = CreateSvcReqDTO.createWorkloadReqDTOToCreateServiceDto(
			moduleCreateWorkloadReqDTO, moduleJobResDTO.getName());

		// 노드포트 연결
		svcService.createNodePortService(createSvcReqDTO);

		return moduleJobResDTO;
	}

	private void createAndSetImageSecret(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		ModuleCredentialReqDTO credentialReqDTO = moduleCreateWorkloadReqDTO.getImage().credentialReqDTO();
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
		return workloadModuleService.getInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public void deleteBatchHobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteBatchJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public List<ModuleWorkloadResDTO> getWorkloadList(String workSpaceName) {
		List<ModuleWorkloadResDTO> workloadList = new ArrayList<>();
		List<ModuleBatchJobResDTO> jobWorkloadList = workloadModuleService.getBatchJobWorkloadList(workSpaceName);
		List<ModuleInteractiveJobResDTO> workloadResList = workloadModuleService.getInteractiveJobWorkloadList(workSpaceName);

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

	public ModuleWorkloadResDTO getUserRecentlyWorkload(String workspaceName, String username) {
		List<ModuleWorkloadResDTO> workloadList = getWorkloadList(workspaceName);
		try {
			return workloadList.stream()
				.filter(workload -> workload.getCreator().equals(username))
				.sorted(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt).reversed())
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
		String pvName = "astrago-dataset-pv-"+ UUID.randomUUID().toString().substring(6);
		String svcName = "astrago-dataset-svc-"+ UUID.randomUUID().toString().substring(6);
		String datasetDeploymentName = "astrago-dataset-" + UUID.randomUUID().toString().substring(6);
		String volumeLabelSelectorName = "dataset-storage-volume-"+ UUID.randomUUID().toString().substring(6);
		String connectTestLabelName = "dataset-connect-test-"+ UUID.randomUUID().toString().substring(6);

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

		//<service-varName>.<namespace>.svc.cluster.local
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
	public List<WorkloadResDTO.UsingDatasetDTO> workloadsUsingDataset(Long id) {
		return workloadModuleService.workloadsUsingDataset(id);
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

	/**
	 * TODO 스토리지 파사드 수정될때마다 같이 수정돼야함 (문제해결필요)
	 * 워크스페이스(namespace)에 볼륨 생성
	 *
	 * @param createVolumeDTO
	 */
	private String createVolume(CreateVolumeDTO createVolumeDTO) {
		return volumeService.createVolume(createVolumeDTO);
	}

	@Override
	public Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType) {
		return workloadModuleService.getJobPod(workspaceName, workloadName, workloadType);
	}
}
