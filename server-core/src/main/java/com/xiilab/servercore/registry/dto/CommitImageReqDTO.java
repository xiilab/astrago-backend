package com.xiilab.servercore.registry.dto;

import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommitImageReqDTO {
	private final String workload;
	private final String workspace;
	private final WorkloadType workloadType;
	private final String imageName;
	private final String imageTag;
}
