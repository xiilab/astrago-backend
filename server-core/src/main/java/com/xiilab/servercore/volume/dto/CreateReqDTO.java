package com.xiilab.servercore.volume.dto;

import com.xiilab.modulek8s.storage.facade.dto.CreateVolumeReqDTO;
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

	public CreateVolumeReqDTO toDto(){
		return CreateVolumeReqDTO.builder()
			.name(getName())
			.storageType(getStorageType())
			.requestVolume(getRequestVolume())
			.description(getDescription())
			.workspaceMetaDataName(getWorkspaceMetaDataName())
			.build();
	}
}
