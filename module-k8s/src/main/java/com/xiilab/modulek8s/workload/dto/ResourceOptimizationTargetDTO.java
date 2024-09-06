package com.xiilab.modulek8s.workload.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceOptimizationTargetDTO {
	private String namespace;
	private String podName;
	private float cpuUsage;
	private float memUsage;
	private float gpuUsage;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ResourceOptimizationTargetDTO that = (ResourceOptimizationTargetDTO)o;
		return Objects.equals(namespace, that.namespace) &&
			Objects.equals(podName, that.podName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, podName);
	}
}
