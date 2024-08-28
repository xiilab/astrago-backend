package com.xiilab.modulek8s.deploy.dto.request;

import lombok.Getter;

@Getter
public class ModifyDeployDTO {
	private String workspaceResourceName;
	private String deployResourceName;
	private String name;
	private String description;
}
