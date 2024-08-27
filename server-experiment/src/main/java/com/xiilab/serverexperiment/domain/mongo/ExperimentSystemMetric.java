package com.xiilab.serverexperiment.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	private Double gpuUsage;
	private Double gpuTemp;
	private double wallTime;
}
