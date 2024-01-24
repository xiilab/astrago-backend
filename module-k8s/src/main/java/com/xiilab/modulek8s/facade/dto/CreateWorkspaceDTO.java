package com.xiilab.modulek8s.facade.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWorkspaceDTO {
	private String name;
	private String description;
	private String creatorName;
	private String creator;
	private List<String> userIds;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;
	private int reqDisk;
}
