package com.xiilab.serverbatch.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceOptimizationReportDTO {
	private int cpuLimit;
	private int memLimit;
	private int gpuLimit;
	private int hour;
	private boolean andYN;
	private LocalDateTime startTime;
	private List<ResourceOptimizationTargetDTO> resourceOptimizationTargets;
	private int optimizationResultCnt;
}
