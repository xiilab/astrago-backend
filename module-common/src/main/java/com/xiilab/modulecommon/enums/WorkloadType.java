package com.xiilab.modulecommon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive"),
	DISTRIBUTED("distributed"),
	DEPLOY("deploy");

	private final String type;
}
