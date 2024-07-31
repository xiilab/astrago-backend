package com.xiilab.modulek8sdb.workspace.dto;

import lombok.Getter;

@Getter
public class InsertWorkspaceDatasetDTO {
	private Long datasetId;
	private String workspaceResourceName;
	private String defaultPath;
}
