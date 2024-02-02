package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateLocalDatasetResDTO {
	private String dns;
	private String deploymentName;
	private String pvcName;
	private String pvName;
	private String svcName;
}
