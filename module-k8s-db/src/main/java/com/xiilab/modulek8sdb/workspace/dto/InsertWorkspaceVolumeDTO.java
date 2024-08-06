package com.xiilab.modulek8sdb.workspace.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InsertWorkspaceVolumeDTO {
	private Long volumeId;
	private String workspaceResourceName;
	private String defaultPath;
	private Set<Long> labelIds;

	@Builder
	public InsertWorkspaceVolumeDTO(
		Long volumeId,
		String workspaceResourceName,
		String defaultPath,
		Set<Long> labelIds) {
		this.volumeId = volumeId;
		this.workspaceResourceName = workspaceResourceName;
		this.defaultPath = defaultPath;
		this.labelIds = labelIds;
	}
}
