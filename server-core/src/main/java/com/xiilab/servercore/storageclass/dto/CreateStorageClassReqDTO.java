package com.xiilab.servercore.storageclass.dto;

import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;

import lombok.Getter;

@Getter
public class CreateStorageClassReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private String ip;
	private String storageSavePath;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;

	public CreateStorageClassReqDTO() {
		super();
	}

	public CreateStorageClassDTO toModuleDto() {
		return CreateStorageClassDTO.builder()
			.name(getName())
			.description(getDescription())
			.creatorId(creatorId)
			.creatorUserName(creatorUserName)
			.creatorFullName(creatorFullName)
			.ip(ip)
			.storageType(storageType)
			.storageSavePath(storageSavePath)
			.build();
	}

	public void setUserInfo(String creatorId, String creatorName, String creatorFullName) {
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}
}
