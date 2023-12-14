package com.xiilab.servercore.storage.volume.dto;

import com.xiilab.modulek8s.storage.facade.dto.StorageReqDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
public class CreateReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;

	public CreateReqDTO() {
		super();
	}

	public StorageReqDTO toDto(){
		return StorageReqDTO.builder()
			.name(getName())
			.storageType(getStorageType())
			.requestVolume(getRequestVolume())
			.description(getDescription())
			.workspaceMetaDataName(getWorkspaceMetaDataName())
			.build();
	}
}
