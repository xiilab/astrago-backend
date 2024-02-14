package com.xiilab.modulek8s.resource_quota.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceQuotaKey {
	REQUEST_CPU_KEY("requests.cpu"),
	REQUEST_MEMORY_KEY("requests.memory"),
	REQUEST_GPU_KEY("requests.nvidia.com/gpu"),
	LIMITS_CPU_KEY("limits.cpu"),
	LIMITS_MEMORY_KEY("limits.memory"),
	LIMITS_GPU_KEY("limits.nvidia.com/gpu");

	private final String key;
}
