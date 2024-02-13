package com.xiilab.servercore.volume.dto;

import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;

import lombok.Getter;

@Getter
public class CreateVolumeReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private String storageClassMetaName;
	private int requestVolume;
	private String workspaceMetaDataName;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;

	public CreateVolumeReqDTO() {
		super();
	}

	public CreateVolumeDTO toModuleDto(){
		return CreateVolumeDTO.builder()
			.name(getName())
			.creatorId(creatorId)
			.creatorUserName(creatorUserName)
			.creatorFullName(creatorFullName)
			.storageClassMetaName(storageClassMetaName)
			.storageType(storageType)
			.requestVolume(requestVolume)
			.workspaceMetaDataName(workspaceMetaDataName)
			.build();
	}

	public void setUserInfo(String creatorId, String creatorName, String creatorFullName){
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}
}
