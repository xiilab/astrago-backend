package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DistributedResourceDTO {
	@Getter
	@Builder
	public static class LauncherInfo {
		private float cpuRequest;
		private float memRequest;
	}

	@Getter
	@Builder
	public static class WorkerInfo {
		private float cpuRequest;
		private float memRequest;
		private int gpuRequest;
		private int workerCnt;
	}
}
