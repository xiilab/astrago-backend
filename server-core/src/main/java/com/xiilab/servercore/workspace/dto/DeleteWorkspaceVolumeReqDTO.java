package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeleteWorkspaceVolumeReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;

	private String creator;
	private String creatorName;

	@Builder
	public DeleteWorkspaceVolumeReqDTO(String workspaceMetaName, String volumeMetaName, String creator, String creatorName) {
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
		this.creator = creator;
		this.creatorName = creatorName;
	}



	public DeleteVolumeDTO toModuleDto(){
		return DeleteVolumeDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creatorId(creator)
			.creatorUserName(creatorName)
			.build();
	}

}
