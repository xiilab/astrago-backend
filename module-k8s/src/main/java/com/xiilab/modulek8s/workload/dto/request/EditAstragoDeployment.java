package com.xiilab.modulek8s.workload.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditAstragoDeployment {
	private String volumeLabelSelectorName;
	private String pvcName;
	private String hostPath;
	private String namespace;
	private String astragoDeploymentName;
}
