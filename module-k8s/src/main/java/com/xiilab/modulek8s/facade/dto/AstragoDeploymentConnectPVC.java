package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AstragoDeploymentConnectPVC {
	private String volumeName;
	private String hostPath;
	private String pvcName;
}
