package com.xiilab.modulek8s.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.dto.ResourceDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
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
	 * batch 워크로드 정보 조회 메소드
	 * astra에서 생성 여부에 대해서 분기 처리
	 *
	 * @param job
	 * @return
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
			LocalDateTime createTime = metadata.getCreationTimestamp() == null ? LocalDateTime.now() : LocalDateTime.parse(metadata.getCreationTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
			LocalDateTime deleteTime = metadata.getDeletionTimestamp() == null ? LocalDateTime.now() : LocalDateTime.parse(metadata.getDeletionTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
			return K8SResourceMetadataDTO.builder()
				.name(AnnotationField.NAME.getField())
				.description(AnnotationField.DESCRIPTION.getField())
				.resourceName(metadata.getName())
				.creatorId(annotations.get(LabelField.CREATOR_ID.getField()))
				.creatorUserName(annotations.get(AnnotationField.CREATOR_USER_NAME.getField()))
				.creatorFullName(annotations.get(AnnotationField.CREATOR_FULL_NAME.getField()))
				.datasetIds(annotations.get(AnnotationField.DATASET_IDS.getField()))
				.modelIds(annotations.get(AnnotationField.MODEL_IDS.getField()))
				.workspaceName(AnnotationField.WORKSPACE_NAME.getField())
				.workspaceResourceName(metadata.getNamespace())
				.cpuReq(containerResourceReq.getCpuReq())
				.memReq(containerResourceReq.getMemReq())
				.gpuReq(containerResourceReq.getGpuReq())
				.imgName(AnnotationField.IMAGE_NAME.getField())
				.imgTag(AnnotationField.IMAGE_TAG.getField())
				.codeIds(annotations.get(AnnotationField.CODE_IDS.getField()))
				// .createdAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getCreationTimestamp())))
				// .deletedAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getDeletionTimestamp())))
				.createdAt(createTime)
				.deletedAt(deleteTime)
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
			.name(metadata.getName())
			.resourceName(metadata.getLabels().get("app"))
			.workspaceName(metadata.getNamespace())
			.workspaceResourceName(metadata.getNamespace())
			.cpuReq(containerResourceReq.getCpuReq())
			.memReq(containerResourceReq.getMemReq())
			.gpuReq(containerResourceReq.getGpuReq())
			.imgName(container.getImage().split(":")[0])
			.imgTag(container.getImage().split(":")[1])
			// .createdAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getCreationTimestamp())))
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
			LocalDateTime createTime = metadata.getCreationTimestamp() == null ? LocalDateTime.now() : LocalDateTime.parse(metadata.getCreationTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
			LocalDateTime deleteTime = metadata.getDeletionTimestamp() == null ? LocalDateTime.now() : LocalDateTime.parse(metadata.getDeletionTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
			return K8SResourceMetadataDTO.builder()
				.name(AnnotationField.NAME.getField())
				.description(AnnotationField.DESCRIPTION.getField())
				.resourceName(metadata.getName())
				.creatorId(annotations.get(LabelField.CREATOR_ID.getField()))
				.creatorUserName(annotations.get(AnnotationField.CREATOR_USER_NAME.getField()))
				.creatorFullName(annotations.get(AnnotationField.CREATOR_FULL_NAME.getField()))
				.workspaceName(AnnotationField.WORKSPACE_NAME.getField())
				.workspaceResourceName(metadata.getNamespace())
				.cpuReq(containerResourceReq.getCpuReq())
				.memReq(containerResourceReq.getMemReq())
				.gpuReq(containerResourceReq.getGpuReq())
				.imgName(AnnotationField.IMAGE_NAME.getField())
				.imgTag(AnnotationField.IMAGE_TAG.getField())
				.datasetIds(annotations.get(AnnotationField.DATASET_IDS.getField()))
				.modelIds(annotations.get(AnnotationField.MODEL_IDS.getField()))
				// .createdAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getCreationTimestamp())))
				// .deletedAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getDeletionTimestamp())))
				.createdAt(createTime)
				.deletedAt(deleteTime)
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
			.name(metadata.getName())
			.resourceName(metadata.getLabels().get("app"))
			.workspaceName(metadata.getNamespace())
			.workspaceResourceName(metadata.getNamespace())
			.cpuReq(containerResourceReq.getCpuReq())
			.memReq(containerResourceReq.getMemReq())
			.gpuReq(containerResourceReq.getGpuReq())
			.imgName(container.getImage().split(":")[0])
			.imgTag(container.getImage().split(":")[1])
			// .createdAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getCreationTimestamp())))
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

				Integer cpuReq = cpu != null ? Integer.valueOf(cpu.getAmount()) : null;
				Integer memReq = mem != null ? Integer.valueOf(mem.getAmount()) : null;
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
		List<Node> items = nodeList.getItems();
		if (CollectionUtils.isEmpty(items)) {
			throw new IllegalStateException("해당 클러스터에 등록된 node가 존재하지 않습니다.");
		}
		int cpu = items.stream()
			.mapToInt(node -> Integer.parseInt(node.getStatus().getCapacity().get("cpu").getAmount()))
			.sum();

		int mem = (int)items.stream()
			.mapToDouble(node -> convertQuantity(node.getStatus().getCapacity().get("memory")))
			.sum();

		int gpu = items.stream()
			.map(node -> node.getStatus().getCapacity().get("nvidia.com/gpu"))
			.flatMapToInt(
				capacity -> capacity != null ? IntStream.of(Integer.parseInt(capacity.getAmount())) : IntStream.empty())
			.sum();

		return new ClusterResourceDTO(cpu, mem, gpu);
	}

	private static double convertQuantity(Quantity quantity) {
		String format = quantity.getFormat();
		double amount = Double.parseDouble(quantity.getAmount());
		if (format.equals("Ki")) {
			return (amount / (1024 * 1024));
		} else if (format.equals("Mi")) {
			return (amount / 1024);
		} else {
			throw new IllegalArgumentException(format + " format은 확인되지 않은 format입니다.");
		}
	}
}
