package com.xiilab.modulek8s.storage.facade.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageReqDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;

	@Builder
	public StorageReqDTO(String name, String description, StorageType storageType, int requestVolume,
		String workspaceMetaDataName) {
		super(name, description);
		this.storageType = storageType;
		this.requestVolume = requestVolume;
		this.workspaceMetaDataName = workspaceMetaDataName;
	}
}
