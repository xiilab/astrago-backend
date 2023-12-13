package com.xiilab.servercore.storage.volume.dto;

import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateVolumeDTO {
	private String volumeName;
	private StorageType storageType;
	private String workspaceMetaDataName;
	private int requestVolume;

	public CreateVolumeDTO(String volumeName, StorageType storageType, String workspaceMetaDataName,
		int requestVolume) {
		this.volumeName = volumeName;
		this.storageType = storageType;
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.requestVolume = requestVolume;
	}
}
