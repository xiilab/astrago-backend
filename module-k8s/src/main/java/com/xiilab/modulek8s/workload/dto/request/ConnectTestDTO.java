package com.xiilab.modulek8s.workload.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectTestDTO {
	private String pvName;
	private String pvcName;
	private String deploymentName;
	private String volumeLabelSelectorName;
	private String connectTestLabelName;
	private String namespace;
	private String hostPath;
}
