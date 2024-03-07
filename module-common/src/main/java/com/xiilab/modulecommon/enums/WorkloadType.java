package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadType {
	BATCH("batch"),
	INTERACTIVE("interactive"),
	DEPLOY("deploy");

	private final String type;
}
