package com.xiilab.servercore.tus.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResModelInfo {
	private Long modelId;
	private String modelVersion;
}
