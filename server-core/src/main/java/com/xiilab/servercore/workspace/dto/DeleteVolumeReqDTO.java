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

	@Builder
	public DeleteVolumeReqDTO(String workspaceMetaName, String volumeMetaName) {
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
	}

	public DeleteVolumeDTO toModuleDto(){
		return DeleteVolumeDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.build();
	}
}
