package com.xiilab.serverexperiment.domain.mongo;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Builder;

@Document(collection = "logs")
@Builder
public class Log {
	@Id
	private String id;
	@Field("workload_id")
	private String workloadId;
	private int step;
	private int epoch;
	private Map<String, Double> metrics;
	private double relativeTime;
	private double wallTime;
}
