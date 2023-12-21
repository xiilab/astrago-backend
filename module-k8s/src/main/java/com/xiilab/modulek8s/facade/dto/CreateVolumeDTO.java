package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.common.enumeration.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateVolumeDTO extends K8SResourceReqDTO {
	private String storageClassMetaName;
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;

	@Builder
	public CreateVolumeDTO(String name, String description, String creatorName, String creator, String storageClassMetaName, StorageType storageType, int requestVolume,
		String workspaceMetaDataName) {
		super(name, description, creatorName, creator);
		this.storageClassMetaName = storageClassMetaName;
		this.storageType = storageType;
		this.requestVolume = requestVolume;
		this.workspaceMetaDataName = workspaceMetaDataName;
	}
}
