package com.xiilab.modulek8s.resource_quota.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotalResourceQuotaDTO {
	private int cpu;
	private int mem;
	private int gpu;
}
