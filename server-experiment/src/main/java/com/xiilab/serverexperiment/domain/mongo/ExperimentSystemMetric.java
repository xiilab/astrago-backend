package com.xiilab.serverexperiment.domain.mongo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.xiilab.serverexperiment.dto.ExperimentDataDTO;

import lombok.Builder;

@Document(collection = "ExperimentSystemMetrics")
@Builder
public class ExperimentSystemMetric {
	@Id
	private String id;
	@Field("workload_id")
	private String workloadId;
	private Double cpuUsage;
	private Double memUsage;
	private List<ExperimentDataDTO.GPUInfo> gpuInfos;
	private double wallTime;
}
