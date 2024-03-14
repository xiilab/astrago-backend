package com.xiilab.servercore.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClusterResourceCompareDTO {
	private int clusterCPU;
	private int clusterMEM;
	private int clusterGPU;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;
}
