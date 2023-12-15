package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeleteVolumeDTO extends K8SResourceReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;

	@Builder
	public DeleteVolumeDTO(String name, String description, String workspaceMetaName, String volumeMetaName) {
		super(name, description);
		this.workspaceMetaName = workspaceMetaName;
		this.volumeMetaName = volumeMetaName;
	}
}
