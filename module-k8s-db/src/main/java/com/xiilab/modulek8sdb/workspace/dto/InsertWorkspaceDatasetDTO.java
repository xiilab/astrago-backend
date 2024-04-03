package com.xiilab.modulek8sdb.workspace.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class InsertWorkspaceDatasetDTO {
	private Long datasetId;
	private String workspaceResourceName;
	private String defaultPath;
}
