package com.xiilab.modulek8s.workload.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourcesUnit {
	GPU_UNIT("Qty"),
	MEM_UNIT("Gi"),
	CPU_UNIT("m");

	private final String unit;
}
