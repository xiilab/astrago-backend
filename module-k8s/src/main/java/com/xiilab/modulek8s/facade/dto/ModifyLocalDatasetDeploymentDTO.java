package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModifyLocalDatasetDeploymentDTO {
	private String deploymentName;
	private String modifyDatasetName;
	private String namespace;
}
