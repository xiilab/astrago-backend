package com.xiilab.modulek8s.storage.storageclass.dto.response;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VolumeDTO {
	private String volumeName;
	private String volumeMetaName;

	@Builder
	public VolumeDTO(String volumeName, String volumeMetaName) {
		this.volumeName = volumeName;
		this.volumeMetaName = volumeMetaName;
	}
	public static VolumeDTO toDTO(PersistentVolumeClaim volume){
		return VolumeDTO.builder()
			.volumeMetaName(volume.getMetadata().getName())
			.volumeName(volume.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
			.build();
	}
}
