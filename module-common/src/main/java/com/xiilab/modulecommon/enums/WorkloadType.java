package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive"),
	DEPLOY("deploy");

	private final String type;
}
