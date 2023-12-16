package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.servercore.common.dto.APIBaseReqDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeleteVolumeReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;

	private String creator;
	private String creatorName;

	@Builder
	public DeleteVolumeReqDTO(String workspaceMetaName, String volumeMetaName, String creator, String creatorName) {
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
		this.creator = creator;
		this.creatorName = creatorName;
	}



	public DeleteVolumeDTO toModuleDto(){
		return DeleteVolumeDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creator(creator)
			.creatorName(creatorName)
			.build();
	}

}
