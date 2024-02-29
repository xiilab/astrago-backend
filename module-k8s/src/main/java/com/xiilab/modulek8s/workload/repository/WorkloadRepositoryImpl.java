package com.xiilab.modulek8s.workload.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
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
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
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
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD);
		}
	}

	@Override
	public Pod getInteractiveJobPod(String workspaceName, String workloadName) throws K8sException {
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
			throw new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD);
		}
	}

	@Override
	public WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id) {
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

	@Override
	public void createModelDeployment(CreateModelDeployment createDeployment) {
		DeploymentVO deployment = DeploymentVO.dtoToEntity(createDeployment);
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resource(deployment.createResource()).create();
		}
	}

	@Override
	public WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String datasetId = "md-" + id;
			List<Job> jobsInUseDataset = getJobsInUseDataset(datasetId, client);
			List<StatefulSet> statefulSetsInUseDataset = getStatefulSetsInUseDataset(datasetId, client);
			List<Deployment> deploymentsInUseDataset = getDeploymentsInUseDataset(datasetId, client);

			List<WorkloadResDTO.UsingModelDTO> workloads = new ArrayList<>();

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
			if (getJobsInUseDataset(label, client).size() == 0 &&
				getStatefulSetsInUseDataset(label, client).size() == 0 &&
				getDeploymentsInUseDataset(label, client).size() == 0) {
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

	private static void getWorkloadInfoUsingDataset(List<WorkloadResDTO.UsingDatasetDTO> workloads,
		HasMetadata hasMetadata,
		WorkloadResourceType resourceType) {
		WorkloadResDTO.UsingDatasetDTO usingDatasetDTO = WorkloadResDTO.UsingDatasetDTO.builder()
			.workloadName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.creator(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_USER_NAME.getField()))
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

	private static void getWorkloadInfoUsingModel(List<WorkloadResDTO.UsingModelDTO> workloads, HasMetadata hasMetadata,
		WorkloadResourceType resourceType) {
		WorkloadResDTO.UsingModelDTO usingModelDTO = WorkloadResDTO.UsingModelDTO.builder()
			.workloadName(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.creator(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATOR_USER_NAME.getField()))
			.createdAt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATED_AT.getField()))
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
			default:
				usingModelDTO.setStatus(null);
		}
		usingModelDTO.setResourceType(resourceType);
		workloads.add(usingModelDTO);
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
			return kubernetesClient.batch()
				.v1()
				.jobs()
				.inAnyNamespace()
				.withLabel(LabelField.CREATOR_ID.getField(), userId)
				.list();
		}
	}

	private DeploymentList getInteractiveJobListByCreator(String userId) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().withLabel(LabelField.CREATOR_ID.getField(), userId).list();
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
