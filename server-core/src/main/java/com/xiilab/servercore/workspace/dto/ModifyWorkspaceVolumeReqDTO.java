package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;

import lombok.Getter;

@Getter
public class ModifyWorkspaceVolumeReqDTO extends APIBaseReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;
	private String creatorId;
	private String creatorName;

	public ModifyWorkspaceVolumeReqDTO() {
		super();
	}

	public ModifyVolumeDTO toModuleDto(){
		return ModifyVolumeDTO.builder()
			.name(getName())
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creator(creatorId)
			.creatorName(creatorName)
			.build();
	}
	public void setMetaNames(String workspaceMetaName, String volumeMetaName){
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
	}
	public void setUserInfo(String creatorId, String creatorName){
		this.creatorId = creatorId;
		this.creatorName = creatorName;
	}
}
