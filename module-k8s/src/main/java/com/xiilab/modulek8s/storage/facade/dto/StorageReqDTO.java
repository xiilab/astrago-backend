package com.xiilab.modulek8s.storage.facade.dto;

import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageReqDTO {
	private String volumeName;
	private StorageType storageType;
	private String workspaceMetaDataName;
	private int requestVolume;

	@Builder
	public StorageReqDTO(String volumeName, StorageType storageType, String workspaceMetaDataName, int requestVolume) {
		this.volumeName = volumeName;
		this.storageType = storageType;
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.requestVolume = requestVolume;
	}
}
