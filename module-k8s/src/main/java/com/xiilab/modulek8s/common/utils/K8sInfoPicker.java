package com.xiilab.modulek8s.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.dto.ResourceDTO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
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
	 * k8s resource에서 필요한 정보를 추출하는 메소드
	 * astra에서 생성한 것과, 서버에서 직접 생성한 것을 분기처리
	 *
	 * @param hasMetadata k8s resource 객체(Job, Deployment...)
	 * @return
	 */
	public static K8SResourceMetadataDTO getMetadataFromResource(HasMetadata hasMetadata) {
		try {
			if (isCreatedByAstra(hasMetadata)) {
				return getMetadataFromAstraResource(hasMetadata);
			} else {
				return getMetadataFromNormalResource(hasMetadata);
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Astra에서 생성되었는지 여부를 판별하는 메소드
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
	 * @param hasMetadata k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getMetadataFromAstraResource(HasMetadata hasMetadata) {
		try {
			ObjectMeta metadata = hasMetadata.getMetadata();
			Map<String, String> annotations = metadata.getAnnotations();
			return K8SResourceMetadataDTO.builder()
				.name(annotations.get("name"))
				.description(annotations.get("description"))
				.resourceName(metadata.getName())
				.creator(annotations.get("creator"))
				.createdAt(LocalDateTime.parse(annotations.get("created-at")))
				.imgName(annotations.get("image-name"))
				.imgTag(annotations.get("image-tag"))
				.deletedAt(convertUnixTimestampToLocalDateTime(Long.parseLong(metadata.getDeletionTimestamp())))
				.build();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 서버에서 생성된 k8s resource의 정보를 추출하는 메소드
	 * Kind에 따라 형변환을 통하여 resource spec에 있는 정보를 추출하여 매핑
	 *
	 * @param hasMetadata k8s resource 객체
	 * @return
	 */
	private static K8SResourceMetadataDTO getMetadataFromNormalResource(HasMetadata hasMetadata) {
		try {
			String kind = hasMetadata.getKind();
			ObjectMeta metadata = hasMetadata.getMetadata();
			K8SResourceMetadataDTO.K8SResourceMetadataDTOBuilder metadataBuilder = K8SResourceMetadataDTO.builder();

			if (isJobOrDeployment(kind)) {
				Container container = getContainerFromHasMetadata(hasMetadata);
				if (container != null) {
					ResourceDTO containerResourceReq = getContainerResourceReq(container);

					metadataBuilder = K8SResourceMetadataDTO.builder()
						.cpuReq(containerResourceReq.getCpuReq())
						.memReq(containerResourceReq.getMemReq())
						.gpuReq(containerResourceReq.getGpuReq())
						.imgName(container.getImage().split(":")[0])
						.imgTag(container.getImage().split(":")[1]);
				}
			}

			return metadataBuilder
				.name(metadata.getName())
				.description(null)
				.resourceName(metadata.getName())
				.creator(null)
				.createdAt(LocalDateTime.parse(metadata.getCreationTimestamp(), DateTimeFormatter.ISO_DATE_TIME))
				.deletedAt(LocalDateTime.now())
				.build();
		} catch (NullPointerException e) {
			return null;
		}
	}

	private static boolean isJobOrDeployment(String kind) {
		return "Job".equals(kind) || "Deployment".equals(kind);
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
				Quantity disk = requests.get("disk");

				Integer cpuReq = cpu != null ? Integer.valueOf(cpu.getAmount()) : null;
				Integer memReq = mem != null ? Integer.valueOf(mem.getAmount()) : null;
				Integer gpuReq = gpu != null ? Integer.valueOf(gpu.getAmount()) : null;
				Integer diskReq = disk != null ? Integer.valueOf(disk.getAmount()) : null;

				return ResourceDTO.builder()
					.cpuReq(cpuReq)
					.memReq(memReq)
					.gpuReq(gpuReq)
					.diskReq(diskReq)
					.build();
			}
		}
		return ResourceDTO.builder().build();
	}
}
