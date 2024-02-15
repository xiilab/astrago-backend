package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive");

	private final String type;
}
