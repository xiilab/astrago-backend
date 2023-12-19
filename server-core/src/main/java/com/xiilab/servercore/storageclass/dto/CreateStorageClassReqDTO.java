package com.xiilab.servercore.storageclass.dto;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Getter;

@Getter
public class CreateStorageClassReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private String ip;
	private String storageSavePath;
	private String creator;
	private String creatorName;

	public CreateStorageClassReqDTO() {
		super();
	}

	public CreateStorageClassDTO toModuleDto(){
		return CreateStorageClassDTO.builder()
			.name(getName())
			.description(getDescription())
			.creator(creator)
			.creatorName(creatorName)
			.ip(ip)
			.storageType(storageType)
			.storageSavePath(storageSavePath)
			.build();
	}
	public void setUserInfo(String creator, String creatorName){
		this.creator = creator;
		this.creatorName = creatorName;
	}
}
