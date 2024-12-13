package com.xiilab.modulek8s.storage.volume.dto.request;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulecommon.enums.StorageType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;
	private String storageClassMetaName;

	public static CreateDTO createVolumeDtoToCreateDto(
		com.xiilab.modulek8s.facade.dto.CreateVolumeDTO createVolumeDTO) {
		return CreateDTO.builder()
			.name(createVolumeDTO.getName())
			.workspaceMetaDataName(createVolumeDTO.getWorkspaceMetaDataName())
			.requestVolume(createVolumeDTO.getRequestVolume())
			.storageType(createVolumeDTO.getStorageType())
			.creatorId(createVolumeDTO.getCreatorId())
			.creatorUserName(createVolumeDTO.getCreatorUserName())
			.build();
	}

	public void setStorageClassMetaName(String storageMetaName) {
		this.storageClassMetaName = storageMetaName;
	}
}
