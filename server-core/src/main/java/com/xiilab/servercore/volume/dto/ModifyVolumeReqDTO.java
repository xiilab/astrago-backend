package com.xiilab.servercore.volume.dto;

import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;

import lombok.Getter;

@Getter
public class ModifyVolumeReqDTO extends APIBaseReqDTO {
	private String volumeMetaName;

	public void setVolumeMetaName(String volumeMetaName){
		this.volumeMetaName = volumeMetaName;
	}
	public ModifyVolumeDTO toModuleDto(){
		return ModifyVolumeDTO.builder()
			.name(getName())
			.volumeMetaName(volumeMetaName)
			.build();
	}
}