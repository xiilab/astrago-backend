package com.xiilab.modulek8s.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.dto.ResourceDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class K8sInfoPicker {
	/**
	 * k8s container에서 환경변수를 조회하는 메소드
	 *
	 * @param container
	 * @return
	 */
	public static Map<String, String> getEnvFromContainer(Container container) {
		try {
			List<EnvVar> env = container.getEnv();
			Map<String, String> map = new HashMap<>();
			env.forEach(envVar -> map.put(envVar.getName(), envVar.getValue()));
			return map;
		} catch (NullPointerException e) {
			log.debug("{} container env 출력 중 npe", container.getName());
			return Collections.emptyMap();
		}
	}

	/**
	 * k8s container에서 환경변수를 조회하는 메소드
	 *
	 * @param container
	 * @return
	 */
	public static Map<String, Integer> getPortFromContainer(Container container) {
		try {
			Map<String, Integer> map = new HashMap<>();
			List<ContainerPort> ports = container.getPorts();
			ports.forEach(port -> map.put(port.getName(), port.getContainerPort()));
			return map;
		} catch (NullPointerException e) {
			log.debug("{} container env 출력 중 npe", container.getName());
			return Collections.emptyMap();
		}
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

	public static WorkloadStatus getWorkloadStatus(DeploymentStatus deploymentStatus) {
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

	private static List<K8SResourceMetadataDTO.Env> getEnvs(List<EnvVar> envs) {
		if (!CollectionUtils.isEmpty(envs)) {
			return envs.stream()
				.map(env -> new K8SResourceMetadataDTO.Env(env.getName(), env.getValue()))
				.toList();
		} else {
			return new ArrayList<>();
		}
	}

	private static List<K8SResourceMetadataDTO.Port> getPorts(List<ContainerPort> ports) {
		if (!CollectionUtils.isEmpty(ports)) {
			return ports.stream()
				.map(port -> new K8SResourceMetadataDTO.Port(port.getName(), port.getContainerPort()))
				.toList();
		} else {
			return new ArrayList<>();
		}
	}

	private static List<K8SResourceMetadataDTO.Code> initializeCodesInfo(List<Container> initContainers) {
		if (!CollectionUtils.isEmpty(initContainers)) {
			return initContainers.stream()
				.map(initContainer -> new K8SResourceMetadataDTO.Code(initContainer.getEnv())).toList();
		} else {
			return new ArrayList<>();
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
