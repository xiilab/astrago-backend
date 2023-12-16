package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateVolumeDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;

	@Builder
	public CreateVolumeDTO(String name, String description, String creatorName, String creator, StorageType storageType, int requestVolume,
		String workspaceMetaDataName) {
		super(name, description, creatorName, creator);
		this.storageType = storageType;
		this.requestVolume = requestVolume;
		this.workspaceMetaDataName = workspaceMetaDataName;
	}
}
