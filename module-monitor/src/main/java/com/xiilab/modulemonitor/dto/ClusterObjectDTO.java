package com.xiilab.modulemonitor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClusterObjectDTO {
	private String namespace;
	private String nodeName;
	private String podName;
	private String deploymentName;
	private String hpaName;
	private String daemonsetName;
	private String statefulsetName;
	private String status;
	private String reason;
	private String roles;
	private String message;
	private String containerName;
	private long restartCount;
}
