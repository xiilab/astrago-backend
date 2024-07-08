package com.xiilab.modulek8s.node.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GpuInfoDTO {
	private String gpuName;
	private int memory;
}
