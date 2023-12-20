package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.common.enumeration.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateStorageClassDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private String ip;
	private String storageSavePath;

	@Builder
	public CreateStorageClassDTO(String name, String description, String creatorName, String creator,
		StorageType storageType, String ip, String storageSavePath) {
		super(name, description, creatorName, creator);
		this.storageType = storageType;
		this.ip = ip;
		this.storageSavePath = storageSavePath;
	}
}
