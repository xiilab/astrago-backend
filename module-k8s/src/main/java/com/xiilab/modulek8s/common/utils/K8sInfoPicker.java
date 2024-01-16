package com.xiilab.modulek8s.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class K8sInfoPicker {

	public static Map<String, String> getEnvFromContainer(Container container) {
		try {
			List<EnvVar> env = container.getEnv();
			Map<String, String> map = new HashMap<>();
			env.forEach(envVar -> map.put(envVar.getName(),envVar.getValue()));
			return map;
		} catch (NullPointerException e) {
			log.debug("{} container env 출력 중 npe", container.getName());
			return null;
		}
	}

	public static K8SResourceMetadataDTO getMetadataFromResource(HasMetadata hasMetadata) {
		try {
			if (isCreatedByAstra(hasMetadata)) {
				return getMetadataFromAstraResource(hasMetadata);
			} else {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static boolean isCreatedByAstra(HasMetadata hasMetadata) {
		try {
			Map<String, String> labels = hasMetadata.getMetadata().getLabels();
			String createdBy = labels.get("control-by");
			return createdBy.equals("astra");
		} catch (NullPointerException e) {
			return false;
		}
	}

	private static K8SResourceMetadataDTO getMetadataFromAstraResource(HasMetadata hasMetadata) {
		try {
			ObjectMeta metadata = hasMetadata.getMetadata();
			Map<String, String> labels = metadata.getLabels();
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

	public static LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		// 또는 다른 특정 ZoneId를 사용하려면 ZoneId.of("ZoneID")를 사용할 수 있습니다.
	}
}
