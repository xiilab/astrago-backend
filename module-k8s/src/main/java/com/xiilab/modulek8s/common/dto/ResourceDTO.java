package com.xiilab.modulek8s.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDTO {
	private Integer cpuReq;
	private Integer memReq;
	private Integer gpuReq;
}
