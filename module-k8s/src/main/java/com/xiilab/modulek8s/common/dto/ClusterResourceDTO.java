package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClusterResourceDTO {
	private final int cpu;
	private final int mem;
	private final Integer gpu;
}
