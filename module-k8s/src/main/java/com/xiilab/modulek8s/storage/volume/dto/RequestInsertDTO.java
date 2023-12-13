package com.xiilab.modulek8s.storage.volume.dto;

import com.xiilab.modulek8s.storage.volume.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RequestInsertDTO {
	private String volumeName;
	private StorageType storageType;
	private String workspaceMetaDataName;
	private int requestVolume;

	@Builder
	public RequestInsertDTO(String volumeName, StorageType storageType, String workspaceMetaDataName, int requestVolume) {
		this.volumeName = volumeName;
		this.storageType = storageType;
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.requestVolume = requestVolume;
	}
}
