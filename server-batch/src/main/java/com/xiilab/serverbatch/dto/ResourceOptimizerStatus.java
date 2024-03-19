package com.xiilab.serverbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceOptimizerStatus {
	private ResourceOptimizationDTO batch;
	private ResourceOptimizationDTO interactive;
}
