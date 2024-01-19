package com.xiilab.modulek8s.workload.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchedulingType {
	DEFAULT("default"),
	BIN_PACKING("binpack-scheduler"),
	GANG("gang");

	private final String type;
}