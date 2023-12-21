package com.xiilab.servercore.volume.dto;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Getter;

@Getter
public class CreateVolumeReqDTO extends APIBaseReqDTO {
	private StorageType storageType;
	private String storageClassMetaName;
	private int requestVolume;
	private String workspaceMetaDataName;
	private String creator;
	private String creatorName;

	public CreateVolumeReqDTO() {
		super();
	}

	public CreateVolumeDTO toModuleDto(){
		return CreateVolumeDTO.builder()
			.name(getName())
			.creator(creator)
			.storageClassMetaName(storageClassMetaName)
			.creatorName(creatorName)
			.storageType(storageType)
			.requestVolume(requestVolume)
			.workspaceMetaDataName(workspaceMetaDataName)
			.build();
	}
	public void setUserInfo(String creator, String creatorName){
		this.creator = creator;
		this.creatorName = creatorName;
	}
}
