package com.xiilab.serverbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceOptimizerStatus {
	private ResourceOptimizationDTO batch;
	private ResourceOptimizationDTO interactive;
}
