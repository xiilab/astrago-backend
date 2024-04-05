package com.xiilab.modulek8s.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDTO {
	private Float cpuReq;
	private Float memReq;
	private Integer gpuReq;
}
