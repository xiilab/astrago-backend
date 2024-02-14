package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModifyLocalModelDeploymentDTO {
	private String deploymentName;
	private String modifyModelName;
	private String namespace;
}
