package com.xiilab.modulek8s.workload.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kubeflow.v2beta1.MPIJob;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDistributedJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.DeploymentVO;
import com.xiilab.modulek8s.workload.vo.DistributedJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;
import com.xiilab.modulek8s.workload.vo.JobCodeVO;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.api.model.events.v1.EventList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkloadRepositoryImpl implements WorkloadRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public CreateJobResDTO createBatchJobWorkload(BatchJobVO batchJobVO) {
		Job resource = (Job)createResource(batchJobVO.createResource());
		Map<Long, Map<String, String>> codesInfoMap = getCodesInfoMap(batchJobVO.getCodes());
		Map<Long, Map<String, String>> datasetInfoMap = getVolumesInfoMap(batchJobVO.getDatasets());
		Map<Long, Map<String, String>> modelInfoMap = getVolumesInfoMap(batchJobVO.getModels());
		return new CreateJobResDTO(resource, codesInfoMap, datasetInfoMap, modelInfoMap);
	}

	@Override
	public CreateJobResDTO createInteractiveJobWorkload(InteractiveJobVO interactiveJobVO) {
		Deployment resource = (Deployment)createResource(interactiveJobVO.createResource());
		Map<Long, Map<String, String>> codesInfoMap = getCodesInfoMap(interactiveJobVO.getCodes());
		Map<Long, Map<String, String>> datasetInfoMap = getVolumesInfoMap(interactiveJobVO.getDatasets());
		Map<Long, Map<String, String>> modelInfoMap = getVolumesInfoMap(interactiveJobVO.getModels());
		return new CreateJobResDTO(resource, codesInfoMap, datasetInfoMap, modelInfoMap);
	}

	@Override
	public CreateJobResDTO createDistributedJobWorkload(DistributedJobVO distributedJobVO) {
		MPIJob resource = (MPIJob)createResource(distributedJobVO.createResource());
		Map<Long, Map<String, String>> codesInfoMap = getCodesInfoMap(distributedJobVO.getCodes());
		Map<Long, Map<String, String>> datasetInfoMap = getVolumesInfoMap(distributedJobVO.getDatasets());
		Map<Long, Map<String, String>> modelInfoMap = getVolumesInfoMap(distributedJobVO.getModels());
		return new CreateJobResDTO(resource, codesInfoMap, datasetInfoMap, modelInfoMap);
	}

	@Override
	public void createConnectTestDeployment(ConnectTestDTO connectTestDTO) {
		DeploymentVO deployment = DeploymentVO.dtoToEntity(connectTestDTO);
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//테스트용 deployment 생성
			kubernetesClient.resource(deployment.createResource()).create();
		}
	}

	@Override
	public boolean testConnectPodIsAvailable(String connectTestLabelName, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//테스트용 pod 조회
			Pod connectPod = kubernetesClient.pods()
				.inNamespace(namespace)
				.withLabel(LabelField.APP.getField(), connectTestLabelName)
				.list()
				.getItems()
				.get(0);
			List<PodCondition> conditions = connectPod.getStatus().getConditions();
			boolean isAvailable = false;
			for (PodCondition condition : conditions) {
				String status = condition.getStatus();
				isAvailable = "true".equalsIgnoreCase(status) ? true : false;
				if (!isAvailable) {
					break;
				}
			}
			return isAvailable;
		}
	}

	@Override
	public void deleteConnectTestDeployment(String deploymentName, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
		}
	}

	@Override
	public void editAstragoDeployment(EditAstragoDeployment editAstragoDeployment) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Volume vol = new VolumeBuilder()
				.withName(editAstragoDeployment.getVolumeLabelSelectorName())
				.withPersistentVolumeClaim(
					new PersistentVolumeClaimVolumeSource(editAstragoDeployment.getPvcName(), false))
				.build();
			kubernetesClient.apps()
				.deployments()
				.inNamespace(editAstragoDeployment.getNamespace())
				.withName(editAstragoDeployment.getAstragoDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editSpec()
					.editOrNewTemplate()
					.editSpec()
					.addAllToVolumes(List.of(vol))
					.editContainer(0)
					.addNewVolumeMount()
					.withName(editAstragoDeployment.getVolumeLabelSelectorName())
					.withMountPath(editAstragoDeployment.getHostPath())
					.endVolumeMount()
					.endContainer()
					.endSpec()
					.endTemplate()
					.endSpec()
					.build());
		}
	}

	@Override
	public void editBatchJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			// kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get()
			kubernetesClient.batch().v1().jobs().inNamespace(workspaceResourceName)
				.withName(workloadResourceName).edit(
					job -> new JobBuilder(job).editMetadata()
						.addToAnnotations(AnnotationField.NAME.getField(), name)
						.addToAnnotations(AnnotationField.DESCRIPTION.getField(), description)
						.endMetadata()
						.build()
				);
		}
	}

	@Override
	public void editInteractiveJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps().deployments().inNamespace(workspaceResourceName)
				.withName(workloadResourceName).edit(
					deployment -> new DeploymentBuilder(deployment).editMetadata()
						.addToAnnotations(AnnotationField.NAME.getField(), name)
						.addToAnnotations(AnnotationField.DESCRIPTION.getField(), description)
						.endMetadata()
						.build()
				);
		}
	}

	// TODO 잡 파드 추가
	@Override
	public ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
		Job job = getBatchJob(workSpaceName, workloadName);
		return new ModuleBatchJobResDTO(job);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		Deployment deployment = getInteractiveJob(workSpaceName, workloadName);
		return new ModuleInteractiveJobResDTO(deployment);
	}

	@Override
	public ModuleDistributedJobResDTO getDistributedJobWorkload(String workSpaceName, String workloadName) {
		MPIJob distributedJob = getDistributedJob(workSpaceName, workloadName);
		return new ModuleDistributedJobResDTO(distributedJob);
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByWorkspaceName(String workSpaceName) {
		JobList batchJobList = getBatchJobList(workSpaceName);
		return batchJobList.getItems().stream()
			.filter(job -> job.getMetadata().getAnnotations().containsKey(LabelField.CONTROL_BY.getField()))
			.map(ModuleBatchJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByCreator(String userId) {
		JobList batchJobList = getBatchJobListByCreator(userId);
		return batchJobList.getItems().stream()
			.filter(job -> job.getMetadata().getAnnotations().containsKey(LabelField.CONTROL_BY.getField()))
			.map(ModuleBatchJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByWorkspaceResourceNameAndCreator(
		String workspaceResourceName, String workloadName) {
		JobList batchJobList = getBatchJobListByWorkspaceResourceNameAndCreator(
			workspaceResourceName, workloadName);
		return batchJobList.getItems().stream()
			.filter(job -> job.getMetadata().getAnnotations().containsKey(LabelField.CONTROL_BY.getField()))
			.map(ModuleBatchJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByWorkspace(String workSpaceName) {
		DeploymentList interactiveJobList = getInteractiveJobList(workSpaceName);
		return interactiveJobList.getItems().stream()
			.map(ModuleInteractiveJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadByCreator(String creator) {
		DeploymentList interactiveJobList = getInteractiveJobListByCreator(creator);
		return interactiveJobList.getItems().stream()
			.map(ModuleInteractiveJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByWorkspaceResourceNameAndCreator(
		String workspaceResourceName, String userId) {
		DeploymentList interactiveJobList = getInteractiveJobListByWorkspaceResourceNameAndCreator(
			workspaceResourceName, userId);
		return interactiveJobList.getItems().stream()
			.map(ModuleInteractiveJobResDTO::new)
			.toList();
	}

	@Override
	public String deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		return deleteJob(workSpaceName, workloadName);
	}

	@Override
	public String deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return deleteInteractiveJob(workSpaceName, workloadName);
	}

	@Override
	public void deleteDistributedWorkload(String workspaceName, String workloadName) {
		deleteMpiJob(workspaceName, workloadName);
	}

	@Override
	public ExecListenable connectBatchJobTerminal(String workspaceName, String workloadName) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		Job job = kubernetesClient.batch().v1().jobs().inNamespace(workspaceName).withName(workloadName).get();
		String app = job.getMetadata().getLabels().get("app");
		String namespace = job.getMetadata().getNamespace();
		Pod pod = kubernetesClient.pods().inNamespace(namespace).withLabel("app", app).list().getItems().get(0);
		return kubernetesClient.pods()
			.inNamespace(workspaceName)
			.withName(pod.getMetadata().getName())
			.redirectingInput()
			.redirectingOutput()
			.redirectingError()
			.withTTY();
	}

	@Override
	public ExecListenable connectInteractiveJobTerminal(String workspaceName, String workloadName) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		Deployment deployment = kubernetesClient.apps()
			.deployments()
			.inNamespace(workspaceName)
			.withName(workloadName)
			.get();
		String app = deployment.getMetadata().getLabels().get("app");
		String namespace = deployment.getMetadata().getNamespace();
		Pod pod = kubernetesClient.pods().inNamespace(namespace).withLabel("app", app).list().getItems().get(0);
		return kubernetesClient.pods()
			.inNamespace(workspaceName)
			.withName(pod.getMetadata().getName())
			.redirectingInput()
			.redirectingOutput()
			.redirectingError()
			.withTTY();
	}

	@Override
	public ExecListenable connectDistributeJobTerminal(String workspaceName, String workloadName) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		MPIJob mpiJob = kubernetesClient.resources(MPIJob.class)
			.inNamespace(workspaceName)
			.withName(workloadName)
			.get();
		Pod pod = kubernetesClient.pods()
			.inNamespace(workspaceName)
			.withLabel("training.kubeflow.org/job-name", mpiJob.getMetadata().getName())
			.withLabel("training.kubeflow.org/job-role", "launcher")
			.list()
			.getItems()
			.get(0);
		return kubernetesClient.pods()
			.inNamespace(workspaceName)
			.withName(pod.getMetadata().getName())
			.redirectingInput()
			.redirectingOutput()
			.redirectingError()
			.withTTY();
	}

	@Override
	public Pod getBatchJobPod(String workspaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Job job = kubernetesClient.batch().v1().jobs().inNamespace(workspaceName).withName(workloadName).get();
			String app = job.getMetadata().getLabels().get("app");
			String namespace = job.getMetadata().getNamespace();
			return kubernetesClient.pods().inNamespace(namespace).withLabel("app", app).list().getItems().get(0);
		} catch (Exception e) {
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD);
		}
	}

	@Override
	public Pod getInteractiveJobPod(String workspaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Deployment deployment = kubernetesClient.apps()
				.deployments()
				.inNamespace(workspaceName)
				.withName(workloadName)
				.get();
			String app = deployment.getMetadata().getLabels().get("app");
			String namespace = deployment.getMetadata().getNamespace();
			return kubernetesClient.pods().inNamespace(namespace).withLabel("app", app).list().getItems().get(0);
		} catch (Exception e) {
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD);
		}
	}

	@Override
	public Pod getDistributedLauncherPod(String workspaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			MPIJob mpiJob = kubernetesClient.resources(MPIJob.class)
				.inNamespace(workspaceName)
				.withName(workloadName)
				.get();
			String app = mpiJob.getMetadata().getLabels().get("app");
			String namespace = mpiJob.getMetadata().getNamespace();
			return kubernetesClient.pods()
				.inNamespace(namespace)
				.withLabel("job-name", app + "-launcher")
				.list()
				.getItems()
				.get(0);
		} catch (Exception e) {
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD);
		}
	}

	private static void getWorkloadInfoUsingDataset(List<WorkloadResDTO.UsingWorkloadDTO> workloads,
		HasMetadata hasMetadata,
		WorkloadResourceType resourceType) {
		WorkloadResDTO.UsingWorkloadDTO usingWorkloadDTO = WorkloadResDTO.UsingWorkloadDTO.builder()
			.workloadName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.resourceName(hasMetadata.getMetadata().getName())
			.workspaceResourceName(hasMetadata.getMetadata().getNamespace())
			.creator(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_USER_NAME.getField()))
			.createdAt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATED_AT.getField()))
			.creatorName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_FULL_NAME.getField()))
			.build();

		switch (resourceType) {
			case JOB:
				Job job = (Job)hasMetadata;
				usingWorkloadDTO.setStatus(getJobStatus(job.getStatus()));
				break;
			case DEPLOYMENT:
				Deployment deployment = (Deployment)hasMetadata;
				usingWorkloadDTO.setStatus(getDeploymentStatus(deployment.getStatus()));
				break;
			case STATEFULSET:
				StatefulSet statefulSet = (StatefulSet)hasMetadata;
				usingWorkloadDTO.setStatus(getStatefulsetStatus(statefulSet.getStatus()));
				break;
			case DISTRIBUTED:
				MPIJob mpiJob = (MPIJob)hasMetadata;
				usingWorkloadDTO.setStatus(K8sInfoPicker.getDistributedWorkloadStatus(mpiJob.getStatus()));
				break;
			default:
				usingWorkloadDTO.setStatus(null);
		}
		usingWorkloadDTO.setResourceType(resourceType);
		workloads.add(usingWorkloadDTO);
	}

	@Override
	public void createDatasetDeployment(CreateDatasetDeployment createDeployment) {
		DeploymentVO deployment = DeploymentVO.dtoToEntity(createDeployment);
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resource(deployment.createResource()).create();
		}
	}

	@Override
	public void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps()
				.deployments()
				.inNamespace(modifyLocalDatasetDeploymentDTO.getNamespace())
				.withName(modifyLocalDatasetDeploymentDTO.getDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editMetadata()
					.addToAnnotations(AnnotationField.DATASET_NAME.getField(),
						modifyLocalDatasetDeploymentDTO.getModifyDatasetName())
					.endMetadata()
					.build());
		}
	}

	private static void getWorkloadInfoUsingModel(List<WorkloadResDTO.UsingWorkloadDTO> workloads,
		HasMetadata hasMetadata,
		WorkloadResourceType resourceType) {
		WorkloadResDTO.UsingWorkloadDTO usingModelDTO = WorkloadResDTO.UsingWorkloadDTO.builder()
			.resourceName(hasMetadata.getMetadata().getName())
			.workspaceResourceName(hasMetadata.getMetadata().getNamespace())
			.workloadName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.creator(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_USER_NAME.getField()))
			.createdAt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATED_AT.getField()))
			.creatorName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_FULL_NAME.getField()))
			.build();

		switch (resourceType) {
			case JOB:
				Job job = (Job)hasMetadata;
				usingModelDTO.setStatus(getJobStatus(job.getStatus()));
				break;
			case DEPLOYMENT:
				Deployment deployment = (Deployment)hasMetadata;
				usingModelDTO.setStatus(getDeploymentStatus(deployment.getStatus()));
				break;
			case STATEFULSET:
				StatefulSet statefulSet = (StatefulSet)hasMetadata;
				usingModelDTO.setStatus(getStatefulsetStatus(statefulSet.getStatus()));
				break;
			case DISTRIBUTED:
				MPIJob mpiJob = (MPIJob)hasMetadata;
				usingModelDTO.setStatus(K8sInfoPicker.getDistributedWorkloadStatus(mpiJob.getStatus()));
				break;
			default:
				usingModelDTO.setStatus(null);
		}
		usingModelDTO.setResourceType(resourceType);
		workloads.add(usingModelDTO);
	}

	@Override
	public void deleteDeploymentByResourceName(String deploymentName, String namespace) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
		}
	}

	@Override
	public void createModelDeployment(CreateModelDeployment createDeployment) {
		DeploymentVO deployment = DeploymentVO.dtoToEntity(createDeployment);
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resource(deployment.createResource()).create();
		}
	}

	private static List<MPIJob> getMPIJobsInUseDataset(String key, KubernetesClient client) {
		MixedOperation<MPIJob, KubernetesResourceList<MPIJob>, Resource<MPIJob>> mpiJobResource = client.resources(
			MPIJob.class);
		return mpiJobResource
			.inAnyNamespace()
			.withLabel(key, "true")
			.list()
			.getItems();
	}

	@Override
	public void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalModelDeploymentDTO) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps()
				.deployments()
				.inNamespace(modifyLocalModelDeploymentDTO.getNamespace())
				.withName(modifyLocalModelDeploymentDTO.getDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editMetadata()
					.addToAnnotations(AnnotationField.DATASET_NAME.getField(),
						modifyLocalModelDeploymentDTO.getModifyModelName())
					.endMetadata()
					.build());
		}
	}

	@Override
	public boolean isUsedModel(Long modelId) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String label = "md-" + modelId;
			if (getJobsInUseVolume(label, client).size() == 0 &&
				getStatefulSetsInUseVolume(label, client).size() == 0 &&
				getDeploymentsInUseVolume(label, client).size() == 0) {
				return false;
			}
			return true;
		}
	}

	@Override
	public List<String> getFileListInWorkloadContainer(String podName, String namespace, String path)
		throws IOException {
		String pattern = MessageFormat.format("stat {0}/* --format=%n,%F,%s,%Y", path);
		return executeCommandToContainer(podName, namespace, pattern);
	}

	@Override
	public List<String> getFileInfoInWorkloadContainer(String podName, String namespace, String path) throws
		IOException {
		String pattern = MessageFormat.format("stat {0} --format=%n,%F,%s,%Y", path);
		return executeCommandToContainer(podName, namespace, pattern);
	}

	@Override
	public int getDirectoryFileCount(String podName, String namespace, String path) throws IOException {
		String pattern = MessageFormat.format("ls {0} -l | grep ^- | wc -l", path);
		List<String> result = executeCommandToContainer(podName, namespace, pattern);
		if (CollectionUtils.isEmpty(result)) {
			return 0;
		}
		try {
			return Integer.parseInt(result.get(0));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 생성된 파드에서 파일을 다운로드 받는 메소드
	 *
	 * @param podName   파드이름
	 * @param namespace namespace
	 * @param filePath  다운받으려고하는 파일 위치
	 * @return 파일 복사 성공 여부
	 */
	@Override
	public CopyOrReadable downloadFileFromPod(String podName, String namespace, String filePath) {
		//fabric8io를 사용하여 해당 프로젝트가 올라가있는 파드에서 파일을 내려받는다.
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		//해당 경로에 있는 파일 객체를 가져온다.
		return kubernetesClient.pods()
			.inNamespace(namespace)
			.withName(podName)
			.file(filePath);
	}

	/**
	 * 생성된 파드에서 폴더를 다운로드 받는 메소드
	 *
	 * @param podName    파드이름
	 * @param namespace  namespace
	 * @param folderPath 다운받으려고하는 파일 위치
	 * @return 파일 복사 성공 여부
	 */
	@Override
	public CopyOrReadable downloadFolderFromPod(String podName, String namespace, String folderPath) {
		//fabric8io를 사용하여 해당 프로젝트가 올라가있는 파드에서 파일을 내려받는다.
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		//해당 경로에 있는 파일 객체를 가져온다.
		return kubernetesClient.pods()
			.inNamespace(namespace)
			.withName(podName)
			.dir(folderPath);
	}

	@Override
	public void deleteFileFromPod(String podName, String namespace, String filePath) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//해당 경로에 있는 파일 객체를 가져온다.
			kubernetesClient.pods()
				.inNamespace(namespace)
				.withName(podName)
				.redirectingInput()
				.redirectingOutput()
				.redirectingError()
				.withTTY()
				.exec("sh", "-c", String.format("rm -rf %s", filePath));
		}
	}

	@Override
	public Boolean uploadFileToPod(String podName, String namespace, String path, File file) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.pods()
				.inNamespace(namespace)
				.withName(podName)
				.file(path + File.separator + file.getName())
				.upload(file.toPath());
		}
	}

	@Override
	public boolean mkdirToPod(String podName, String namespace, String path) {
		List<String> result = executeCommandToContainer(podName, namespace, String.format("mkdir %s", path));
		if (!CollectionUtils.isEmpty(result)) {
			if (result.get(0).contains("No such file or directory")) {
				return false;
			}
		}
		return true;
	}

	private List<String> executeCommandToContainer(String podName, String namespace, String command) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		ExecWatch execWatch = kubernetesClient.pods()
			.inNamespace(namespace)
			.withName(podName)
			.redirectingInput()
			.redirectingOutput()
			.redirectingError()
			.withTTY()
			.exec("sh", "-c", command);
		// InputStream output = execWatch.getOutput();
		// 		// //bufferedReader생성
		// 		// BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(output));
		// 		// //bufferedReader를 읽기전 백업을 위해 mark를 지정한다.
		// 		// bufferedReader.mark(262144);
		// 		// //한줄을 읽어 해당 줄에 해당 문구가 존재하거나 Null이라면 빈 배열로 리턴한다.
		// 		// String readLine = bufferedReader.readLine();
		// 		// if (readLine == null) {
		// 		// 	return null;
		// 		// }
		// 		// //검증을 통과 했다면 reset하여 한줄을 읽기전 상태로 되돌린다.
		// 		// bufferedReader.reset();
		// 		// //가져온 reader를 dto로 매핑하여 리턴한다.
		// 		// return bufferedReader.lines().toList();
		// 스트림에서 데이터를 읽어오기 위한 버퍼드 리더
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(execWatch.getOutput()));
		List<String> lines = new ArrayList<>();

		String line;
		try {
			// 스트림에서 한 줄씩 읽어오기
			while ((line = bufferedReader.readLine()) != null) {
				// 각 줄을 리스트에 추가
				lines.add(line);
			}
		} catch (IOException e) {
			// 예외 처리
		} finally {
			// ExecWatch 정리
			execWatch.close();
		}

		return lines;
	}

	@Override
	public List<AbstractModuleWorkloadResDTO> getAstraBatchWorkload() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Job> items = kubernetesClient.batch()
				.v1()
				.jobs()
				.withLabels(Map.of(LabelField.CONTROL_BY.getField(), "astra"))
				.list()
				.getItems();
			return items.stream().map(ModuleBatchJobResDTO::new).collect(Collectors.toList());
		}
	}

	@Override
	public List<AbstractModuleWorkloadResDTO> getAstraInteractiveWorkload() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Deployment> items = kubernetesClient.apps()
				.deployments()
				.withLabels(Map.of(LabelField.CONTROL_BY.getField(), "astra"))
				.list()
				.getItems();
			return items.stream().map(ModuleInteractiveJobResDTO::new).collect(Collectors.toList());
		}
	}

	@Override
	public boolean optimizationResource(String pod, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Pod podResult = kubernetesClient.pods().inNamespace(namespace).withName(pod).get();

			OwnerReference controllerUid = KubernetesResourceUtil.getControllerUid(podResult);

			if (controllerUid != null) {
				String ownerKind = controllerUid.getKind();
				String ownerName = controllerUid.getName();
				if ("ReplicaSet".equals(ownerKind)) {
					ReplicaSet replicaSet = kubernetesClient.apps()
						.replicaSets()
						.inNamespace(namespace)
						.withName(ownerName)
						.get();
					if (replicaSet != null) {
						OwnerReference deployController = KubernetesResourceUtil.getControllerUid(replicaSet);
						if ("Deployment".equals(deployController.getKind())) {
							Deployment deployment = kubernetesClient.apps()
								.deployments()
								.inNamespace(namespace)
								.withName(deployController.getName())
								.get();
							kubernetesClient.resource(deployment).delete();
							log.info("deployment {}가 삭제되었습니다.", deployment.getMetadata().getName());
						}
					}
				} else if ("Job".equals(ownerKind)) {
					Job job = kubernetesClient.batch().v1().jobs().inNamespace(namespace).withName(ownerName).get();
					job.getMetadata();
				} else if("service".equals(ownerKind)) {
					Service service = kubernetesClient.services().inNamespace(namespace).withName(ownerName).get();
					kubernetesClient.resource(service).delete();
					log.info("service {}가 삭제되었습니다.", service.getMetadata().getName());
				}
			}
		} catch (KubernetesClientException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public AbstractModuleWorkloadResDTO getParentController(String pod, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Pod podResult = kubernetesClient.pods().inNamespace(namespace).withName(pod).get();

			OwnerReference controllerUid = KubernetesResourceUtil.getControllerUid(podResult);
			if (controllerUid != null) {
				String ownerKind = controllerUid.getKind();
				String ownerName = controllerUid.getName();
				if ("ReplicaSet".equals(ownerKind)) {
					ReplicaSet replicaSet = kubernetesClient.apps()
						.replicaSets()
						.inNamespace(namespace)
						.withName(ownerName)
						.get();
					if (replicaSet != null) {
						OwnerReference deployController = KubernetesResourceUtil.getControllerUid(replicaSet);
						if ("Deployment".equals(deployController.getKind())) {
							Deployment deployment = kubernetesClient.apps()
								.deployments()
								.inNamespace(namespace)
								.withName(deployController.getName())
								.get();
							return new ModuleInteractiveJobResDTO(deployment);
						}
					}
				} else if ("Job".equals(ownerKind)) {
					Job job = kubernetesClient.batch().v1().jobs().inNamespace(namespace).withName(ownerName).get();
					return new ModuleBatchJobResDTO(job);
				}
			}
		}
		return null;
	}

	@Override
	public List<Event> getWorkloadEventList(String pod, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			EventList list = kubernetesClient.events().v1().events().inNamespace(namespace).list();
			List<Event> items = list.getItems();
			if (Objects.nonNull(items)) {
				return items.stream()
					.filter(item -> item.getRegarding().getName().contains(pod))
					.toList();
			}
			return new ArrayList<>();
		}
	}

	@Override
	public Map<String, Event> getWorkloadRecentlyEvent(List<String> workloadNames, String workspaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			EventList eventList = kubernetesClient.events().v1().events().inNamespace(workspaceName).list();
			List<Event> items = eventList.getItems();
			Map<String, Event> latestEventsByWorkloadName = new HashMap<>();

			if (!CollectionUtils.isEmpty(items)) {
				items.sort(Comparator.comparing((Event e) -> e.getMetadata().getCreationTimestamp())
					.thenComparingLong(e -> Long.parseLong(e.getMetadata().getResourceVersion())));

				for (Event item : items) {
					String eventName = item.getRegarding().getName();
					workloadNames.stream()
						.filter(eventName::contains)
						.findFirst()
						.ifPresent(workloadName -> latestEventsByWorkloadName.put(workloadName, item));
				}
			}

			workloadNames.forEach(workloadName -> latestEventsByWorkloadName.putIfAbsent(workloadName, null));
			return latestEventsByWorkloadName;
		}
	}

	@Override
	public WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String datasetId = "ds-" + id;
			List<Job> jobsInUseDataset = getJobsInUseVolume(datasetId, client);
			List<StatefulSet> statefulSetsInUseDataset = getStatefulSetsInUseVolume(datasetId, client);
			List<Deployment> deploymentsInUseDataset = getDeploymentsInUseVolume(datasetId, client);
			List<MPIJob> mpiJobsInUseDataset = getMPIJobsInUseDataset(datasetId, client);

			List<WorkloadResDTO.UsingWorkloadDTO> workloads = new ArrayList<>();
			//워크로드 이름(사용자가 지정한 이름), 상태, job Type, 생성자 이름, 생성일자
			for (Job job : jobsInUseDataset) {
				getWorkloadInfoUsingDataset(workloads, job, WorkloadResourceType.JOB);
			}
			for (StatefulSet statefulSet : statefulSetsInUseDataset) {
				getWorkloadInfoUsingDataset(workloads, statefulSet, WorkloadResourceType.STATEFULSET);
			}
			for (Deployment deployment : deploymentsInUseDataset) {
				getWorkloadInfoUsingDataset(workloads, deployment, WorkloadResourceType.DEPLOYMENT);
			}
			for (MPIJob mpiJob : mpiJobsInUseDataset) {
				getWorkloadInfoUsingDataset(workloads, mpiJob, WorkloadResourceType.DISTRIBUTED);
			}
			int totalCount = workloads.size();
			int startIndex = (pageNo - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalCount);

			if (startIndex >= totalCount || endIndex <= startIndex) {
				// 페이지 범위를 벗어나면 빈 리스트 반환
				return WorkloadResDTO.PageUsingDatasetDTO.builder()
					.usingWorkloads(null)
					.totalCount(totalCount)
					.build();
			}

			return WorkloadResDTO.PageUsingDatasetDTO.builder()
				.usingWorkloads(workloads.subList(startIndex, endIndex))
				.totalCount(totalCount)
				.build();
		}
	}

	@Override
	public WorkloadResDTO.PageUsingVolumeDTO workloadsUsingVolume(Integer pageNo, Integer pageSize, Long id) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String volumeId = "vl-" + id;
			List<Job> jobsInUseVolume = getJobsInUseVolume(volumeId, client);
			List<StatefulSet> statefulSetsInUseVolume = getStatefulSetsInUseVolume(volumeId, client);
			List<Deployment> deploymentsInUseVolume = getDeploymentsInUseVolume(volumeId, client);
			List<MPIJob> mpiJobsInUseDataset = getMPIJobsInUseDataset(volumeId, client);

			List<WorkloadResDTO.UsingWorkloadDTO> workloads = new ArrayList<>();
			//워크로드 이름(사용자가 지정한 이름), 상태, job Type, 생성자 이름, 생성일자
			for (Job job : jobsInUseVolume) {
				getWorkloadInfoUsingDataset(workloads, job, WorkloadResourceType.JOB);
			}
			for (StatefulSet statefulSet : statefulSetsInUseVolume) {
				getWorkloadInfoUsingDataset(workloads, statefulSet, WorkloadResourceType.STATEFULSET);
			}
			for (Deployment deployment : deploymentsInUseVolume) {
				getWorkloadInfoUsingDataset(workloads, deployment, WorkloadResourceType.DEPLOYMENT);
			}
			for (MPIJob mpiJob : mpiJobsInUseDataset) {
				getWorkloadInfoUsingDataset(workloads, mpiJob, WorkloadResourceType.DISTRIBUTED);
			}
			int totalCount = workloads.size();
			int startIndex = (pageNo - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalCount);

			if (startIndex >= totalCount || endIndex <= startIndex) {
				// 페이지 범위를 벗어나면 빈 리스트 반환
				return WorkloadResDTO.PageUsingVolumeDTO.builder()
					.usingWorkloads(null)
					.totalCount(totalCount)
					.build();
			}

			return WorkloadResDTO.PageUsingVolumeDTO.builder()
				.usingWorkloads(workloads.subList(startIndex, endIndex))
				.totalCount(totalCount)
				.build();
		}
	}

	@Override
	public boolean isUsedDataset(Long datasetId) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String label = "ds-" + datasetId;
			if (getJobsInUseVolume(label, client).size() == 0 &&
				getStatefulSetsInUseVolume(label, client).size() == 0 &&
				getDeploymentsInUseVolume(label, client).size() == 0 &&
				getMPIJobsInUseDataset(label, client).size() == 0) {
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean isUsedVolume(Long volumeId) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String label = "vl-" + volumeId;
			if (getJobsInUseVolume(label, client).size() == 0 &&
				getStatefulSetsInUseVolume(label, client).size() == 0 &&
				getDeploymentsInUseVolume(label, client).size() == 0 &&
				getMPIJobsInUseDataset(label, client).size() == 0) {
				return false;
			}
			return true;
		}
	}

	private static WorkloadStatus getJobStatus(JobStatus jobStatus) {
		Integer active = jobStatus.getActive();
		Integer failed = jobStatus.getFailed();
		Integer ready = jobStatus.getReady();
		if (failed != null && failed > 0) {
			return WorkloadStatus.ERROR;
		} else if (ready != null && ready > 0) {
			return WorkloadStatus.RUNNING;
		} else if (active != null && active > 0) {
			return WorkloadStatus.PENDING;
		} else {
			return WorkloadStatus.END;
		}
	}

	private static WorkloadStatus getDeploymentStatus(DeploymentStatus deploymentStatus) {
		Integer replicas = deploymentStatus.getReplicas();
		Integer availableReplicas = deploymentStatus.getAvailableReplicas();
		Integer unavailableReplicas = deploymentStatus.getUnavailableReplicas();
		if (unavailableReplicas != null && unavailableReplicas > 0) {
			return WorkloadStatus.ERROR;
		} else if (availableReplicas != null && Objects.equals(replicas, availableReplicas)) {
			return WorkloadStatus.RUNNING;
		} else {
			return WorkloadStatus.PENDING;
		}
	}

	private static WorkloadStatus getStatefulsetStatus(StatefulSetStatus statefulSetStatus) {
		Integer replicas = statefulSetStatus.getReplicas();
		Integer availableReplicas = statefulSetStatus.getAvailableReplicas();
		Integer readyReplicas = statefulSetStatus.getReadyReplicas();
		if (readyReplicas != null || readyReplicas == 0) {
			return WorkloadStatus.ERROR;
		} else if (availableReplicas != null && Objects.equals(replicas, availableReplicas)) {
			return WorkloadStatus.RUNNING;
		} else {
			return WorkloadStatus.PENDING;
		}
	}

	private static List<Job> getJobsInUseVolume(String key, KubernetesClient client) {
		return client.batch().v1().jobs().inAnyNamespace().withLabelIn(key, "true")
			.list()
			.getItems();
	}

	private static List<Deployment> getDeploymentsInUseVolume(String key, KubernetesClient client) {
		return client.apps().deployments().inAnyNamespace().withLabelIn(key, "true")
			.list()
			.getItems();
	}

	private static List<StatefulSet> getStatefulSetsInUseVolume(String key, KubernetesClient client) {
		return client
			.apps()
			.statefulSets()
			.inAnyNamespace()
			.withLabelIn(key, "true")
			.list()
			.getItems();
	}

	@Override
	public WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String datasetId = "md-" + id;
			List<Job> jobsInUseDataset = getJobsInUseVolume(datasetId, client);
			List<StatefulSet> statefulSetsInUseDataset = getStatefulSetsInUseVolume(datasetId, client);
			List<Deployment> deploymentsInUseDataset = getDeploymentsInUseVolume(datasetId, client);
			List<MPIJob> mpiJobsInUseDataset = getMPIJobsInUseDataset(datasetId, client);

			List<WorkloadResDTO.UsingWorkloadDTO> workloads = new ArrayList<>();

			//워크로드 이름(사용자가 지정한 이름), 상태, job Type, 생성자 이름, 생성일자
			for (Job job : jobsInUseDataset) {
				getWorkloadInfoUsingModel(workloads, job, WorkloadResourceType.JOB);
			}
			for (StatefulSet statefulSet : statefulSetsInUseDataset) {
				getWorkloadInfoUsingModel(workloads, statefulSet, WorkloadResourceType.STATEFULSET);
			}
			for (Deployment deployment : deploymentsInUseDataset) {
				getWorkloadInfoUsingModel(workloads, deployment, WorkloadResourceType.DEPLOYMENT);
			}
			for (MPIJob mpiJob : mpiJobsInUseDataset) {
				getWorkloadInfoUsingModel(workloads, mpiJob, WorkloadResourceType.DISTRIBUTED);
			}
			int totalCount = workloads.size();
			int startIndex = (pageNo - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalCount);

			if (startIndex >= totalCount || endIndex <= startIndex) {
				// 페이지 범위를 벗어나면 빈 리스트 반환
				return WorkloadResDTO.PageUsingModelDTO.builder()
					.usingWorkloads(null)
					.totalCount(totalCount)
					.build();
			}
			return WorkloadResDTO.PageUsingModelDTO.builder()
				.usingWorkloads(workloads.subList(startIndex, endIndex))
				.totalCount(totalCount)
				.build();
		}
	}

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}

	@Override
	public Job getBatchJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	@Override
	public Deployment getInteractiveJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	@Override
	public List<Pod> getWorkloadsByWorkloadName(String resourceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.pods().inAnyNamespace().withLabel("app", resourceName).list().getItems();
		}
	}

	public MPIJob getDistributedJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resources(MPIJob.class).inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	private JobList getBatchJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).list();
		}
	}

	private JobList getBatchJobListByCreator(String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch()
				.v1()
				.jobs()
				.inAnyNamespace()
				.withLabel(LabelField.CREATOR_ID.getField(), userId)
				.list();
		}
	}

	private JobList getBatchJobListByWorkspaceResourceNameAndCreator(String workspaceResourceName, String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch()
				.v1()
				.jobs()
				.inNamespace(workspaceResourceName)
				.withLabel(LabelField.CREATOR_ID.getField(), userId)
				.list();
		}
	}

	private DeploymentList getInteractiveJobListByCreator(String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps()
				.deployments()
				.inAnyNamespace()
				.withLabel(LabelField.CREATOR_ID.getField(), userId)
				.list();
		}
	}

	private DeploymentList getInteractiveJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).list();
		}
	}

	private DeploymentList getInteractiveJobListByWorkspaceResourceNameAndCreator(String workspaceResourceName,
		String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps()
				.deployments()
				.inNamespace(workspaceResourceName)
				.withLabel(LabelField.CREATOR_ID.getField(), userId)
				.list();
		}
	}

	private String deleteJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).delete();
			return workloadName;
		}
	}

	private String deleteInteractiveJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).delete();
			return workloadName;
		}
	}

	private void deleteMpiJob(String workspaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resources(MPIJob.class).inNamespace(workspaceName).withName(workloadName).delete();
		}
	}

	private Map<Long, Map<String, String>> getCodesInfoMap(List<JobCodeVO> jobCodeVOList) {
		Map<Long, Map<String, String>> codesMap = new HashMap<>();
		for (JobCodeVO jobCodeVO : jobCodeVOList) {
			Long id = jobCodeVO.id();
			String mountPath = jobCodeVO.mountPath();
			String branch = jobCodeVO.branch();
			codesMap.computeIfAbsent(jobCodeVO.id(), k -> new HashMap<>()).put("mountPath", mountPath);
			codesMap.get(id).put("branch", branch);
		}

		return codesMap;
	}

	private Map<Long, Map<String, String>> getVolumesInfoMap(List<JobVolumeVO> jobVolumeVOList) {
		Map<Long, Map<String, String>> volumesMap = new HashMap<>();
		for (JobVolumeVO jobVolumeVO : jobVolumeVOList) {
			Long id = jobVolumeVO.id();
			String mountPath = jobVolumeVO.mountPath();
			volumesMap.computeIfAbsent(jobVolumeVO.id(), k -> new HashMap<>()).put("mountPath", mountPath);
		}

		return volumesMap;
	}
}
