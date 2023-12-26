package com.xiilab.servercore.workspace.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceApplicationForm {
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private String creatorName;
	private String creator;
	private List<String> userIds;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;
	private int reqDisk;
}
