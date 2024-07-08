package com.xiilab.modulecommon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive"),
	DISTRIBUTED("distributed"),
	SERVICE("service"),
	;

	private final String type;
}
