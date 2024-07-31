package com.xiilab.modulek8sdb.workspace.dto;

import lombok.Getter;

@Getter
public class InsertWorkspaceVolumeDTO {
	private Long volumeId;
	private String workspaceResourceName;
	private String defaultPath;
}
