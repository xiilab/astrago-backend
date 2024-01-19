package com.xiilab.modulek8s.workload.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive"),
	SERVICE("service");

	private final String type;
}
