package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteLocalModelDTO {
	private String deploymentName;
	private String svcName;
	private String pvName;
	private String pvcName;
	private String namespace;

}
