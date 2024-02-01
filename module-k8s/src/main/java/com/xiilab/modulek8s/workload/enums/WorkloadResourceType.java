package com.xiilab.modulek8s.workload.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum WorkloadResourceType {
	JOB,
	STATEFULSET,
	DEPLOYMENT;
}
