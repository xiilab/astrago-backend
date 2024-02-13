package com.xiilab.modulek8s.workload.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.DeploymentVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkloadRepositoryImpl implements WorkloadRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public ModuleJobResDTO createBatchJobWorkload(BatchJobVO batchJobVO) {
		Job resource = (Job)createResource(batchJobVO.createResource());
		return new ModuleJobResDTO(resource);
	}

	@Override
	public ModuleJobResDTO createInteractiveJobWorkload(InteractiveJobVO interactiveJobVOJobVO) {
		Deployment resource = (Deployment)createResource(interactiveJobVOJobVO.createResource());
		return new ModuleJobResDTO(resource);
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
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByWorkspaceName(String workSpaceName) {
		JobList batchJobList = getBatchJobList(workSpaceName);
		return batchJobList.getItems().stream()
			.map(ModuleBatchJobResDTO::new)
			.toList();
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByCreator(String userId) {
		JobList batchJobList = getBatchJobListByCreator(userId);
		return batchJobList.getItems().stream()
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
	public String deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		return deleteJob(workSpaceName, workloadName);
	}

	@Override
	public String deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return deleteInteractiveJob(workSpaceName, workloadName);
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
	public Pod getBatchJobPod(String workspaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Job job = kubernetesClient.batch().v1().jobs().inNamespace(workspaceName).withName(workloadName).get();
			String app = job.getMetadata().getLabels().get("app");
			String namespace = job.getMetadata().getNamespace();
			return kubernetesClient.pods().inNamespace(namespace).withLabel("app", app).list().getItems().get(0);
		} catch (NullPointerException e) {
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_BATCH_JOB_LOG);
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
		} catch (NullPointerException e) {
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_INTERACTIVE_JOB_LOG);
		}
	}

	@Override
	public List<WorkloadResDTO.UsingDatasetDTO> workloadsUsingDataset(Long id) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String datasetId = "ds-" + id;
			List<Job> jobsInUseDataset = getJobsInUseDataset(datasetId, client);
			List<StatefulSet> statefulSetsInUseDataset = getStatefulSetsInUseDataset(datasetId, client);
			List<Deployment> deploymentsInUseDataset = getDeploymentsInUseDataset(datasetId, client);

			List<WorkloadResDTO.UsingDatasetDTO> workloads = new ArrayList<>();
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
			return workloads;
		}
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

	@Override
	public boolean isUsedDataset(Long datasetId) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String label = "ds-" + datasetId;
			if (getJobsInUseDataset(label, client).size() == 0 &&
				getStatefulSetsInUseDataset(label, client).size() == 0 &&
				getDeploymentsInUseDataset(label, client).size() == 0) {
				return false;
			}
			return true;
		}
	}

	@Override
	public void deleteDeploymentByResourceName(String deploymentName, String namespace) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
		}
	}

	private static void getWorkloadInfoUsingDataset(List<WorkloadResDTO.UsingDatasetDTO> workloads,
		HasMetadata hasMetadata,
		WorkloadResourceType resourceType) {
		WorkloadResDTO.UsingDatasetDTO usingDatasetDTO = WorkloadResDTO.UsingDatasetDTO.builder()
			.workloadName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.creator(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_NAME.getField()))
			.createdAt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATED_AT.getField()))
			.build();

		switch (resourceType) {
			case JOB:
				Job job = (Job)hasMetadata;
				usingDatasetDTO.setStatus(getJobStatus(job.getStatus()));
				break;
			case DEPLOYMENT:
				Deployment deployment = (Deployment)hasMetadata;
				usingDatasetDTO.setStatus(getDeploymentStatus(deployment.getStatus()));
				break;
			case STATEFULSET:
				StatefulSet statefulSet = (StatefulSet)hasMetadata;
				usingDatasetDTO.setStatus(getStatefulsetStatus(statefulSet.getStatus()));
				break;
			default:
				usingDatasetDTO.setStatus(null);
		}
		usingDatasetDTO.setResourceType(resourceType);
		workloads.add(usingDatasetDTO);
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

	private static List<Job> getJobsInUseDataset(String key, KubernetesClient client) {
		return client.batch().v1().jobs().withLabelIn(key, "true")
			.list()
			.getItems();
	}

	private static List<Deployment> getDeploymentsInUseDataset(String key, KubernetesClient client) {
		return client.apps().deployments().withLabelIn(key, "true")
			.list()
			.getItems();
	}

	private static List<StatefulSet> getStatefulSetsInUseDataset(String key, KubernetesClient client) {
		return client
			.apps()
			.statefulSets()
			.withLabelIn(key, "true")
			.list()
			.getItems();
	}



	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}

	private Job getBatchJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	private Deployment getInteractiveJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	private JobList getBatchJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).list();
		}
	}

	private JobList getBatchJobListByCreator(String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().withLabel(LabelField.CREATOR.getField(), userId).list();
		}
	}

	private DeploymentList getInteractiveJobListByCreator(String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().withLabel(LabelField.CREATOR.getField(), userId).list();
		}
	}

	private DeploymentList getInteractiveJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).list();
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

}
