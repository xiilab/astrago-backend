package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteStorageReqDTO {
	private String pvcName;
	private String pvName;
	private String volumeName;
	private String hostPath;
	private String namespace;
	private String astragoDeploymentName;
}
