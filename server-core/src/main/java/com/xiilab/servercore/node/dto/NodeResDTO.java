package com.xiilab.servercore.node.dto;

import java.util.Map;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class NodeResDTO {

	@Getter
	@SuperBuilder
	public static class FindGpuResources extends NodeResDTO {
		Map<String, NodeResDTO.GPUInfo> normalGpuMap;
		Map<String, NodeResDTO.GPUInfo> migGpuMap;
		Map<String, NodeResDTO.GPUInfo> mpsGpuMap;
	}

	@Getter
	@SuperBuilder
	public static class GPUInfo {
		private String onePerMemory;
		private Integer maximumGpuCount;
		private Integer totalGpuCount;
		private Boolean useAllGPUStatus;
	}

}
