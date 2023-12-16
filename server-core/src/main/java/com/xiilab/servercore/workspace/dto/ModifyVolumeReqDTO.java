package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Getter;

@Getter
public class ModifyVolumeReqDTO extends APIBaseReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;
	private String creator;
	private String creatorName;

	public ModifyVolumeReqDTO() {
		super();
	}

	public ModifyVolumeDTO toModuleDto(){
		return ModifyVolumeDTO.builder()
			.name(getName())
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creator(creator)
			.creatorName(creatorName)
			.build();
	}
	public void setMetaNames(String workspaceMetaName, String volumeMetaName){
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
	}
	public void setUserInfo(String creator, String creatorName){
		this.creator = creator;
		this.creatorName = creatorName;
	}
}
