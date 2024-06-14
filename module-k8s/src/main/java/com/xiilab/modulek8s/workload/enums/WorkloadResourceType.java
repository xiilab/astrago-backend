package com.xiilab.modulek8s.workload.enums;

import lombok.Getter;

@Getter
public enum WorkloadResourceType {
	JOB,
	STATEFULSET,
	DEPLOYMENT,
	DISTRIBUTED
}
