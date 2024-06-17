package com.xiilab.servercore.node.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class NodeResDTO {

	@Getter
	@SuperBuilder
	public static class FindGpuResources extends NodeResDTO {
		Map<String, List<NodeResDTO.GPUInfo>> normalGpuMap;
		Map<String, List<NodeResDTO.GPUInfo>> migGpuMap;
		Map<String, List<NodeResDTO.GPUInfo>> mpsGpuMap;
	}

	@Getter
	@SuperBuilder
	public static class GPUInfo {
		private String nodeName;
		private String onePerMemory;
		private Integer gpuCount;
		private Integer maximumGpuCount;
		private boolean useAllGPUStatus;
	}

}
