package com.xiilab.serverbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceOptimizerStatus {
	private ResourceOptimizationDTO batch;
	private ResourceOptimizationDTO interactive;
}
