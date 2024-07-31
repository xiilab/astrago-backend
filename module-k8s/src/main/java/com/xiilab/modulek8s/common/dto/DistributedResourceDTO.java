package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// @AllArgsConstructor
public class DistributedResourceDTO {
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class LauncherInfo {
		private float cpuRequest;
		private float memRequest;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WorkerInfo {
		private float cpuRequest;
		private float memRequest;
		private int gpuRequest;
		private int workerCnt;
	}
}
