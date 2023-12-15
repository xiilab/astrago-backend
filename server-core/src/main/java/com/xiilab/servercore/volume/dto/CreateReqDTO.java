package com.xiilab.servercore.volume.dto;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Getter;

@Getter
public class CreateReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;

	public CreateReqDTO() {
		super();
	}

	public CreateVolumeDTO toModuleDto(){
		return CreateVolumeDTO.builder()
			.name(getName())
			.storageType(getStorageType())
			.requestVolume(getRequestVolume())
			.description(getDescription())
			.workspaceMetaDataName(getWorkspaceMetaDataName())
			.build();
	}
}
