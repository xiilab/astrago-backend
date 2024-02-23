package com.xiilab.serverbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceOptimizerStatus {
	private boolean batch;
	private boolean interactive;
}
