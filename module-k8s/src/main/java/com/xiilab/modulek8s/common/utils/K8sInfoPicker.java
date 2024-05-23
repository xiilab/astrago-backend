package com.xiilab.modulek8s.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kubeflow.v2beta1.MPIJob;
import org.kubeflow.v2beta1.MPIJobStatus;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.Spec;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Containers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.InitContainers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Resources;
import org.kubeflow.v2beta1.mpijobstatus.Conditions;
import org.kubeflow.v2beta1.mpijobstatus.ReplicaStatuses;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.dto.ResourceDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.DistributedJobRole;
import com.xiilab.modulek8s.common.enumeration.LabelField;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobCondition;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class K8sInfoPicker {

	/**
	 * 해당 k8s resource가 astrago에서 생성되었는지 체크하는 메소드
	 *
	 * @param resource 조회 할 k8s resource
	 * @return true -> astrago에서 생성한 resource, false -> astrago에서 생성하지 않은 resource
	 */
	public static boolean isAstragoResource(HasMetadata resource) {
		return resource.getMetadata().getName().contains("wl-");
	}

	/**
	 * 두 k8s resource를 비교하여, resource version이 변경되었는지 체크 -> resource version이 달라지면 update 되었다는 것을 의미
	 *
	 * @param resource1 before resource info
	 * @param resource2 after resource info
	 * @return true -> 변경됨, false -> 변경되지않음
	 */
	public static boolean isResourceUpdate(HasMetadata resource1, HasMetadata resource2) {
		return !Objects.equals(resource1.getMetadata().getResourceVersion(),
			resource2.getMetadata().getResourceVersion());
	}

	public static boolean isResourceUpdate(MPIJob mpijob1, MPIJob mpiJob2) {
		return !Objects.equals(mpijob1.getMetadata().getResourceVersion(),
			mpiJob2.getMetadata().getResourceVersion());
	}

	/**
	 * batch 워크로드 정보 조회 메소드
	 * astra에서 생성 여부에 대해서 분기 처리
	 *
	 * @param job
	 */
	public static K8SResourceMetadataDTO getBatchWorkloadInfoFromResource(Job job) {
		K8SResourceMetadataDTO k8SResourceMetadataDTO;
		if (isCreatedByAstra(job)) {
			k8SResourceMetadataDTO = getBatchWorkloadFromAstraResource(job);
		} else {
			k8SResourceMetadataDTO = getBatchWorkloadInfoFromNormalResource(job);
		}
		return k8SResourceMetadataDTO;
	}

	/**
	 * interactive 워크로드 정보 조회 메소드
	 * astra에서 생성 여부에 대해서 분기 처리
	 *
	 * @param deployment
	 * @return
	 */
	public static K8SResourceMetadataDTO getInteractiveWorkloadInfoFromResource(Deployment deployment) {
		K8SResourceMetadataDTO k8SResourceMetadataDTO;
		if (isCreatedByAstra(deployment)) {
			k8SResourceMetadataDTO = getInteractiveWorkloadInfoFromAstraResource(deployment);
		} else {
			k8SResourceMetadataDTO = getInteractiveWorkloadInfoFromNormalResource(deployment);
		}
		return k8SResourceMetadataDTO;
	}

	public static K8SResourceMetadataDTO getDistirubtedWorkloadInfoFromResource(MPIJob mpiJob) {
		K8SResourceMetadataDTO k8SResourceMetadataDTO;
		if (isCreatedByAstra(mpiJob)) {
			k8SResourceMetadataDTO = getDistributedWorkloadFromAstraResource(mpiJob);
		} else {
			k8SResourceMetadataDTO = getDistirubtedWorkloadInfoFromResource(mpiJob);
		}
		return k8SResourceMetadataDTO;
	}

	/**
	 * Astra에서 생성되었는지 여부를 판별하는 메소드
	 *
	 * @param hasMetadata k8s resource 객체
	 * @return
	 */
	public static boolean isCreatedByAstra(HasMetadata hasMetadata) {
		try {
			Map<String, String> labels = hasMetadata.getMetadata().getLabels();
			String createdBy = labels.get("control-by");
			return createdBy.equals("astra");
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 * job annotation에 등록되어있는 argsMap을 가져오는 메소드
	 *
	 * @param annotationMap k8s annotation map
	 * @return
	 */
	public static Map<String, String> getParameterMap(Map<String, String> annotationMap) {
		String argStr = annotationMap.get(AnnotationField.PARAMETER.getField());
		if (StringUtils.isEmpty(argStr)) {
			return null;
		}
		return JsonConvertUtil.convertJsonToMap(argStr);
	}

	/**
	 * astra에서 생성된 resource의 경우 값을 추출하는 메소드
	 * astra에서 생성된 메소드의 경우 metadata에 정보를 저장하기에 해당 정보를 조회하여 매핑
	 *
	 * @param job k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getBatchWorkloadFromAstraResource(Job job) {
		try {
			ObjectMeta metadata = job.getMetadata();
			Map<String, String> annotations = metadata.getAnnotations();
			Container container = getContainerFromHasMetadata(job);
			ResourceDTO containerResourceReq = getContainerResourceReq(container);
			List<Container> initContainers = job.getSpec().getTemplate().getSpec().getInitContainers();
			Map<String, String> mountAnnotationMap = job.getSpec().getTemplate().getMetadata().getAnnotations();
			List<K8SResourceMetadataDTO.Code> codes = initializeCodesInfo(initContainers);
			LocalDateTime createTime = metadata.getCreationTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getCreationTimestamp());
			LocalDateTime deleteTime = metadata.getDeletionTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getDeletionTimestamp());

			return K8SResourceMetadataDTO.builder()
				.uid(metadata.getUid())
				.workloadName(annotations.get(AnnotationField.NAME.getField()))
				.workloadResourceName(metadata.getName())
				.workspaceName(annotations.get(AnnotationField.WORKSPACE_NAME.getField()))
				.workspaceResourceName(metadata.getNamespace())
				.description(annotations.get(AnnotationField.DESCRIPTION.getField()))
				.workloadType(WorkloadType.BATCH)
				.imageId(StringUtils.hasText(annotations.get(AnnotationField.IMAGE_ID.getField())) ?
					Long.parseLong(annotations.get(AnnotationField.IMAGE_ID.getField())) : null)
				.imageType(ImageType.valueOf(annotations.get(AnnotationField.IMAGE_TYPE.getField())))
				.imageName(annotations.get(AnnotationField.IMAGE_NAME.getField()))
				.imageCredentialId(
					StringUtils.hasText(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) ?
						Long.parseLong(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) : null)
				.createdAt(createTime)
				.deletedAt(deleteTime)
				.creatorId(metadata.getLabels().get(LabelField.CREATOR_ID.getField()))
				.creatorUserName(annotations.get(AnnotationField.CREATOR_USER_NAME.getField()))
				.creatorFullName(annotations.get(AnnotationField.CREATOR_FULL_NAME.getField()))
				.cpuReq(containerResourceReq.getCpuReq())
				.memReq(containerResourceReq.getMemReq())
				.gpuReq(containerResourceReq.getGpuReq())
				.datasetIds(annotations.get(AnnotationField.DATASET_IDS.getField()))
				.modelIds(annotations.get(AnnotationField.MODEL_IDS.getField()))
				.envs(getEnvs(container.getEnv()))
				.ports(getPorts(container.getPorts()))
				.workingDir(container.getWorkingDir())
				.parameter(getParameterMap(annotations))
				.codes(codes)
				.datasetMountPathMap(getDatasetAndModelMountMap("ds-", mountAnnotationMap))
				.modelMountPathMap(getDatasetAndModelMountMap("md-", mountAnnotationMap))
				.codeMountPathMap(getCodeMountMap(codes))
				.command(CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2))
				.build();

		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * astra에서 생성된 resource의 경우 값을 추출하는 메소드
	 * astra에서 생성된 메소드의 경우 metadata에 정보를 저장하기에 해당 정보를 조회하여 매핑
	 *
	 * @param mpiJob k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getDistributedWorkloadFromAstraResource(MPIJob mpiJob) {
		try {
			ObjectMeta metadata = mpiJob.getMetadata();
			Map<String, String> annotations = metadata.getAnnotations();
			Containers container = getContainerFromMpiJob(mpiJob);
			ResourceDTO containerResourceReq = getContainersResourceReq(container);
			List<InitContainers> initContainers = mpiJob.getSpec()
				.getMpiReplicaSpecs()
				.get(DistributedJobRole.LAUNCHER.getName())
				.getTemplate()
				.getSpec()
				.getInitContainers();
			Map<String, String> mountAnnotationMap = mpiJob.getMetadata().getAnnotations();
			List<K8SResourceMetadataDTO.Code> codes = initializeCodesInfo(initContainers);
			LocalDateTime createTime = metadata.getCreationTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getCreationTimestamp());
			LocalDateTime deleteTime = metadata.getDeletionTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getDeletionTimestamp());

			return K8SResourceMetadataDTO.builder()
				.uid(metadata.getUid())
				.workloadName(annotations.get(AnnotationField.NAME.getField()))
				.workloadResourceName(metadata.getName())
				.workspaceName(annotations.get(AnnotationField.WORKSPACE_NAME.getField()))
				.workspaceResourceName(metadata.getNamespace())
				.description(annotations.get(AnnotationField.DESCRIPTION.getField()))
				.workloadType(WorkloadType.DISTRIBUTED)
				.imageId(StringUtils.hasText(annotations.get(AnnotationField.IMAGE_ID.getField())) ?
					Long.parseLong(annotations.get(AnnotationField.IMAGE_ID.getField())) : null)
				.imageType(ImageType.valueOf(annotations.get(AnnotationField.IMAGE_TYPE.getField())))
				.imageName(annotations.get(AnnotationField.IMAGE_NAME.getField()))
				.imageCredentialId(
					StringUtils.hasText(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) ?
						Long.parseLong(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) : null)
				.createdAt(createTime)
				.deletedAt(deleteTime)
				.creatorId(metadata.getLabels().get(LabelField.CREATOR_ID.getField()))
				.creatorUserName(annotations.get(AnnotationField.CREATOR_USER_NAME.getField()))
				.creatorFullName(annotations.get(AnnotationField.CREATOR_FULL_NAME.getField()))
				.cpuReq(containerResourceReq.getCpuReq())
				.memReq(containerResourceReq.getMemReq())
				.gpuReq(containerResourceReq.getGpuReq())
				.datasetIds(annotations.get(AnnotationField.DATASET_IDS.getField()))
				.modelIds(annotations.get(AnnotationField.MODEL_IDS.getField()))
				.envs(getEnvs(container.getEnv()))
				.ports(getPorts(container.getPorts()))
				.workingDir(container.getWorkingDir())
				.parameter(getParameterMap(annotations))
				.codes(codes)
				.datasetMountPathMap(getDatasetAndModelMountMap("ds-", mountAnnotationMap))
				.modelMountPathMap(getDatasetAndModelMountMap("md-", mountAnnotationMap))
				.codeMountPathMap(getCodeMountMap(codes))
				.command(CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2))
				.build();

		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 서버에서 생성된 k8s resource의 정보를 추출하는 메소드
	 *
	 * @param job k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getBatchWorkloadInfoFromNormalResource(Job job) {
		ObjectMeta metadata = job.getMetadata();
		Container container = getContainerFromHasMetadata(job);
		ResourceDTO containerResourceReq = getContainerResourceReq(container);
		return K8SResourceMetadataDTO.builder()
			.workloadResourceName(metadata.getName())
			.workspaceResourceName(metadata.getNamespace())
			.cpuReq(containerResourceReq.getCpuReq())
			.memReq(containerResourceReq.getMemReq())
			.gpuReq(containerResourceReq.getGpuReq())
			.imageName(container.getImage())
			.createdAt(LocalDateTime.parse(metadata.getCreationTimestamp(), DateTimeFormatter.ISO_DATE_TIME))
			.deletedAt(LocalDateTime.now())
			.build();
	}

	/**
	 * astra에서 생성된 resource의 경우 값을 추출하는 메소드
	 * astra에서 생성된 메소드의 경우 metadata에 정보를 저장하기에 해당 정보를 조회하여 매핑
	 *
	 * @param deployment k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getInteractiveWorkloadInfoFromAstraResource(Deployment deployment) {
		try {
			ObjectMeta metadata = deployment.getMetadata();
			Map<String, String> annotations = metadata.getAnnotations();
			Container container = getContainerFromHasMetadata(deployment);
			ResourceDTO containerResourceReq = getContainerResourceReq(container);
			List<Container> initContainers = deployment.getSpec().getTemplate().getSpec().getInitContainers();
			Map<String, String> mountAnnotationMap = deployment.getSpec().getTemplate().getMetadata().getAnnotations();
			List<K8SResourceMetadataDTO.Code> codes = initializeCodesInfo(initContainers);
			LocalDateTime createTime = metadata.getCreationTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getCreationTimestamp());
			LocalDateTime deleteTime = metadata.getDeletionTimestamp() == null ? LocalDateTime.now() :
				DateUtils.convertK8sUtcTimeString(metadata.getDeletionTimestamp());
			return K8SResourceMetadataDTO.builder()
				.uid(metadata.getUid())
				.workloadName(annotations.get(AnnotationField.NAME.getField()))
				.workloadResourceName(metadata.getName())
				.workspaceName(annotations.get(AnnotationField.WORKSPACE_NAME.getField()))
				.workspaceResourceName(metadata.getNamespace())
				.description(annotations.get(AnnotationField.DESCRIPTION.getField()))
				.workloadType(WorkloadType.INTERACTIVE)
				.imageId(StringUtils.hasText(annotations.get(AnnotationField.IMAGE_ID.getField())) ?
					Long.parseLong(annotations.get(AnnotationField.IMAGE_ID.getField())) : null)
				.imageType(ImageType.valueOf(annotations.get(AnnotationField.IMAGE_TYPE.getField())))
				.imageName(annotations.get(AnnotationField.IMAGE_NAME.getField()))
				.imageCredentialId(
					StringUtils.hasText(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) ?
						Long.parseLong(annotations.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) : null)
				.createdAt(createTime)
				.deletedAt(deleteTime)
				.creatorId(metadata.getLabels().get(LabelField.CREATOR_ID.getField()))
				.creatorUserName(annotations.get(AnnotationField.CREATOR_USER_NAME.getField()))
				.creatorFullName(annotations.get(AnnotationField.CREATOR_FULL_NAME.getField()))
				.cpuReq(containerResourceReq.getCpuReq())
				.memReq(containerResourceReq.getMemReq())
				.gpuReq(containerResourceReq.getGpuReq())
				.datasetIds(annotations.get(AnnotationField.DATASET_IDS.getField()))
				.modelIds(annotations.get(AnnotationField.MODEL_IDS.getField()))
				.envs(getEnvs(container.getEnv()))
				.ports(getPorts(container.getPorts()))
				.codes(codes)
				.datasetMountPathMap(getDatasetAndModelMountMap("ds-", mountAnnotationMap))
				.modelMountPathMap(getDatasetAndModelMountMap("md-", mountAnnotationMap))
				.codeMountPathMap(getCodeMountMap(codes))
				.command(CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2))
				.ide(annotations.get(AnnotationField.IDE.getField()))
				.build();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 서버에서 생성된 k8s resource의 정보를 추출하는 메소드
	 *
	 * @param deployment k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getInteractiveWorkloadInfoFromNormalResource(Deployment deployment) {
		ObjectMeta metadata = deployment.getMetadata();
		Container container = getContainerFromHasMetadata(deployment);
		ResourceDTO containerResourceReq = getContainerResourceReq(container);
		return K8SResourceMetadataDTO.builder()
			.workloadResourceName(metadata.getName())
			.workspaceResourceName(metadata.getNamespace())
			.cpuReq(containerResourceReq.getCpuReq())
			.memReq(containerResourceReq.getMemReq())
			.gpuReq(containerResourceReq.getGpuReq())
			.imageName(container.getImage())
			.createdAt(LocalDateTime.parse(metadata.getCreationTimestamp(), DateTimeFormatter.ISO_DATE_TIME))
			.deletedAt(LocalDateTime.now())
			.build();
	}

	private static Container getContainerFromHasMetadata(HasMetadata hasMetadata) {
		PodSpec podSpec = getPodSpecFromHasMetadata(hasMetadata);
		if (podSpec != null && !CollectionUtils.isEmpty(podSpec.getContainers())) {
			return podSpec.getContainers().get(0);
		}
		return null;
	}

	private static PodSpec getPodSpecFromHasMetadata(HasMetadata hasMetadata) {
		if (hasMetadata instanceof Job job) {
			return job.getSpec().getTemplate().getSpec();
		} else if (hasMetadata instanceof Deployment deployment) {
			return deployment.getSpec().getTemplate().getSpec();
		}
		return null;
	}

	private static Containers getContainerFromMpiJob(MPIJob mpiJob) {
		Spec specFromMpiJob = getSpecFromMpiJob(mpiJob);
		if (specFromMpiJob != null && !CollectionUtils.isEmpty(specFromMpiJob.getContainers())) {
			return specFromMpiJob.getContainers().get(0);
		}
		return null;
	}

	private static Spec getSpecFromMpiJob(MPIJob mpiJob) {
		return mpiJob.getSpec().getMpiReplicaSpecs().get(DistributedJobRole.WORKER.getName()).getTemplate().getSpec();
	}

	public static LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		// 또는 다른 특정 ZoneId를 사용하려면 ZoneId.of("ZoneID")를 사용할 수 있습니다.
	}

	/**
	 * 컨테이너에 할당된 자원을 조회하는 메소드
	 * req가 설정되어있지 않다면 Null을 리턴함.
	 *
	 * @param container k8s container 객체
	 * @return
	 */
	public static ResourceDTO getContainerResourceReq(Container container) {
		ResourceRequirements resources = container.getResources();
		if (resources != null) {
			Map<String, Quantity> requests = resources.getRequests();
			if (requests != null) {
				Quantity cpu = requests.get("cpu");
				Quantity mem = requests.get("memory");
				Quantity gpu = requests.get("nvidia.com/gpu");

				Float cpuReq = cpu != null ? convertQuantity(cpu) : null;
				Float memReq = mem != null ? convertQuantity(mem) : null;
				Integer gpuReq = gpu != null ? Integer.valueOf(gpu.getAmount()) : null;

				return ResourceDTO.builder()
					.cpuReq(cpuReq)
					.memReq(memReq)
					.gpuReq(gpuReq)
					.build();
			}
		}
		return ResourceDTO.builder().build();
	}

	/**
	 * 컨테이너에 할당된 자원을 조회하는 메소드
	 * req가 설정되어있지 않다면 Null을 리턴함.
	 *
	 * @param container k8s container 객체
	 * @return
	 */
	public static ResourceDTO getContainersResourceReq(Containers container) {
		Resources resources = container.getResources();
		if (resources != null) {
			Map<String, IntOrString> requests = resources.getRequests();
			if (requests != null) {
				IntOrString cpu = requests.get("cpu");
				IntOrString mem = requests.get("memory");
				IntOrString gpu = requests.get("nvidia.com/gpu");

				Float cpuReq = cpu != null ? Float.valueOf(cpu.getStrVal()) : null;
				Float memReq = mem != null ? Float.valueOf(mem.getStrVal().split("Gi")[0]) : null;
				Integer gpuReq = gpu != null ? gpu.getIntVal() : null;

				return ResourceDTO.builder()
					.cpuReq(cpuReq)
					.memReq(memReq)
					.gpuReq(gpuReq)
					.build();
			}
		}
		return ResourceDTO.builder().build();
	}

	public static ClusterResourceDTO getClusterResource(NodeList nodeList) {
		int cpu = 0;
		int mem = 0;
		int gpu = 0;
		List<Node> items = nodeList.getItems();
		if (CollectionUtils.isEmpty(items)) {
			throw new IllegalStateException("해당 클러스터에 등록된 node가 존재하지 않습니다.");
		}

		for (Node node : items) {
			cpu += Integer.parseInt(node.getStatus().getCapacity().get("cpu").getAmount());
			mem += convertQuantity(node.getStatus().getCapacity().get("memory"));
			//MIG가 설정된 경우
			if (node.getMetadata().getLabels().containsKey("nvidia.com/mig.config")) {
				int capacityGPU = node.getStatus().getCapacity().get("nvidia.com/gpu") == null ?
					0 : Integer.parseInt(node.getStatus().getCapacity().get("nvidia.com/gpu").getAmount());
				if (capacityGPU == 0) {
					int migCount = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/mig-count"));
					gpu += migCount;
				} else {
					gpu += capacityGPU;
				}
				//MIG이 불가능한 GPU이거나 GPU가 없는 경우
			} else {
				gpu += node.getStatus().getCapacity().get("nvidia.com/gpu") == null ?
					0 : Integer.parseInt(node.getStatus().getCapacity().get("nvidia.com/gpu").getAmount());
			}
		}

		return new ClusterResourceDTO(cpu, mem, gpu);
	}

	public static float convertQuantity(Quantity quantity) {
		String format = quantity.getFormat();
		float amount = Float.parseFloat(quantity.getAmount());

		if (!StringUtils.hasText(format) || format.equals("Gi")) {
			// Gi는 이미 GB 단위이므로, 직접 반환
			return amount;
		} else if (format.equals("Ki")) {
			// KiB -> GB 변환
			return (float)(amount / (1024.0 * 1024.0 * 1024.0 / 1024.0));
		} else if (format.equals("Mi")) {
			// MiB -> GB 변환
			return (amount / 1024);
		} else if (format.equals("m")) {
			// CPU 자원인 m은 GB로 변환하는 것이 적절하지 않으므로, 예외 처리가 필요할 수 있음
			// 여기서는 단순 예제로 m을 1000으로 나눈 값을 반환
			return (amount / 1000);
		} else if (format.equals("M")) {
			// "M"이 실제로 메모리 단위로 사용될 경우 (Mebibyte 가정), GB로 변환
			return (amount / 1024);
		} else {
			// 확인되지 않은 단위에 대한 예외 처리
			throw new IllegalArgumentException(format + " format은 확인되지 않은 format입니다.");
		}
	}

	public static WorkloadStatus getInteractiveWorkloadStatus(DeploymentStatus deploymentStatus) {
		int replicas = deploymentStatus.getReplicas() == null ? 0 : deploymentStatus.getReplicas();
		int availableReplicas =
			deploymentStatus.getAvailableReplicas() == null ? 0 : deploymentStatus.getAvailableReplicas();
		int unavailableReplicas =
			deploymentStatus.getUnavailableReplicas() == null ? 0 : deploymentStatus.getUnavailableReplicas();
		if (unavailableReplicas > 0) {
			return WorkloadStatus.PENDING;
		} else if (replicas == availableReplicas) {
			return WorkloadStatus.RUNNING;
		} else {
			return WorkloadStatus.ERROR;
		}
	}

	public static WorkloadStatus getBatchWorkloadStatus(JobStatus status) {
		if (status == null) {
			return WorkloadStatus.PENDING;
		}

		List<JobCondition> conditions = status.getConditions();

		// ERROR 상태: 실패한 조건(conditions)이 있는 경우
		if (jobHasFailedCondition(conditions)) {
			return WorkloadStatus.ERROR;
		}

		// RUNNING 상태: 활성 팟(active)이 있고, 성공적으로 완료된 팟(succeeded)이 아직 없는 경우
		if (jobIsRunning(status, conditions)) {
			return WorkloadStatus.RUNNING;
		}

		// PENDING 상태: 작업이 시작되었지만 pod가 아직 시작되지 않았거나 초기 상태인 경우
		if (jobIsPending(status, conditions)) {
			return WorkloadStatus.PENDING;
		}

		// END 상태: 성공적으로 완료된 팟(succeeded)이 있는 경우
		if (jobIsCompleted(status)) {
			return WorkloadStatus.END;
		}

		// 기본적으로 PENDING 상태로 분류
		return WorkloadStatus.PENDING;
	}

	public static WorkloadStatus getDistributedWorkloadStatus(MPIJobStatus mpiJobStatus) {
		if (mpiJobStatus == null) {
			return WorkloadStatus.PENDING;
		}

		if (mpiJobStatus.getCompletionTime() != null) {
			return WorkloadStatus.END;
		}

		boolean hasRunningCondition = mpiJobStatus.getConditions().stream()
			.anyMatch(condition ->
				"Running".equals(condition.getType()) &&
					Conditions.Status.TRUE.equals(condition.getStatus()));

		if (hasRunningCondition) {
			return WorkloadStatus.RUNNING;
		}

		if (getMpiJobFailedYN(mpiJobStatus.getReplicaStatuses())) {
			return WorkloadStatus.ERROR;
		}
		return WorkloadStatus.PENDING;
	}

	public static boolean isBatchJobYN(Job job) {
		return job.getMetadata().getAnnotations().get("type").equals("BATCH");
	}

	private static boolean getMpiJobFailedYN(Map<String, ReplicaStatuses> replicaStatuses) {
		boolean launcherFailedYN = getMpiJobReplicasFailedYN(
			replicaStatuses.get(DistributedJobRole.LAUNCHER.getName()));
		boolean workerFailedYN = getMpiJobReplicasFailedYN(
			replicaStatuses.get(DistributedJobRole.WORKER.getName()));
		return launcherFailedYN || workerFailedYN;
	}

	private static boolean getMpiJobReplicasFailedYN(ReplicaStatuses replicaStatuses) {
		if (replicaStatuses == null) {
			return false;
		}
		if (replicaStatuses.getFailed() == null) {
			return false;
		}
		return replicaStatuses.getFailed() > 0;
	}

	private static boolean jobHasFailedCondition(List<JobCondition> conditions) {
		return conditions.stream()
			.anyMatch(condition -> "Failed".equals(condition.getType()));
	}

	private static boolean jobIsRunning(JobStatus status, List<JobCondition> conditions) {
		Integer active = status.getActive();
		Integer ready = status.getReady();
		Integer succeeded = status.getSucceeded();

		return (active != null && active > 0) && (ready != null && ready > 0) && (succeeded == null || succeeded == 0)
			&& !jobHasFailedCondition(conditions)
			&& !jobHasPodInitializingCondition(conditions);
	}

	private static boolean jobIsPending(JobStatus status, List<JobCondition> conditions) {
		Integer active = status.getActive();
		Integer failed = status.getFailed();
		Integer succeeded = status.getSucceeded();

		return status.getStartTime() != null
			&& ((active == null || active == 0) && (failed == null && succeeded == null))
			&& !jobHasFailedCondition(conditions)
			&& jobHasPodInitializingCondition(conditions);
	}

	private static boolean jobIsCompleted(JobStatus status) {
		Integer succeeded = status.getSucceeded();
		return succeeded != null && succeeded > 0;
	}

	private static boolean jobHasPodInitializingCondition(List<JobCondition> conditions) {
		return conditions.stream()
			.anyMatch(condition -> "PodInitializing".equals(condition.getType()));
	}

	private static List<K8SResourceMetadataDTO.Env> getEnvs(List<? extends KubernetesResource> envs) {
		if (!CollectionUtils.isEmpty(envs)) {
			return envs.stream()
				.map(K8SResourceMetadataDTO.Env::new)
				.toList();
		} else {
			return new ArrayList<>();
		}
	}

	private static List<K8SResourceMetadataDTO.Port> getPorts(List<? extends KubernetesResource> ports) {
		if (!CollectionUtils.isEmpty(ports)) {
			return ports.stream()
				.map(K8SResourceMetadataDTO.Port::new)
				.toList();
		} else {
			return new ArrayList<>();
		}
	}

	private static List<K8SResourceMetadataDTO.Code> initializeCodesInfo(
		List<? extends KubernetesResource> containers) {
		List<K8SResourceMetadataDTO.Code> codes = new ArrayList<>();
		if (!CollectionUtils.isEmpty(containers)) {
			codes.addAll(
				containers.stream()
					.map(K8sInfoPicker::extractCodeFromContainer)
					.toList());
		}

		return codes;
	}

	private static K8SResourceMetadataDTO.Code extractCodeFromContainer(Object container) {
		List<? extends KubernetesResource> envVars = null;
		if (container instanceof Container containerInstance) {
			envVars = containerInstance.getEnv();
		} else if (container instanceof Containers containersInstance) {
			envVars = containersInstance.getEnv();
		} else if (container instanceof InitContainers initContainers) {
			envVars = initContainers.getEnv();
		}
		if (envVars != null) {
			return new K8SResourceMetadataDTO.Code(envVars);
		} else {
			return null;
		}
	}

	private static Map<Long, String> getDatasetAndModelMountMap(String startsWithName,
		Map<String, String> annotations) {
		Map<Long, String> result = new HashMap<>();
		annotations.entrySet().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.startsWith(startsWithName)) {
				result.put(Long.parseLong(key.split("-")[1]), value);
			}
		});
		return result;
	}

	private static Map<String, Map<String, String>> getCodeMountMap(List<K8SResourceMetadataDTO.Code> codes) {
		Map<String, Map<String, String>> codesMap = new HashMap<>();
		for (K8SResourceMetadataDTO.Code code : codes) {
			String mountPath = code.getMountPath();
			String branch = code.getBranch();
			codesMap.computeIfAbsent(code.getRepositoryUrl(), k -> new HashMap<>()).put("mountPath", mountPath);
			codesMap.get(code.getRepositoryUrl()).put("branch", branch);
		}

		return codesMap;
	}
}
