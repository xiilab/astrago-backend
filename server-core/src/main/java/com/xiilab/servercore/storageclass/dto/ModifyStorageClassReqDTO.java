package com.xiilab.servercore.storageclass.dto;

import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Getter;

@Getter
public class ModifyStorageClassReqDTO extends APIBaseReqDTO {
	private String storageClassMetaName;

	public void setStorageClassMetaName(String storageClassMetaName) {
		this.storageClassMetaName = storageClassMetaName;
	}

	public ModifyStorageClassDTO toModuleDto() {
		return ModifyStorageClassDTO.builder()
			.name(getName())
			.storageClassMetaName(storageClassMetaName)
			.description(getDescription())
			.build();
	}
}
