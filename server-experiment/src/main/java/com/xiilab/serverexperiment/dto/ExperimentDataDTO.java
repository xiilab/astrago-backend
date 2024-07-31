package com.xiilab.serverexperiment.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ExperimentDataDTO {

	@Getter
	public static class Req {
		private String uuid;
		private String workloadName;
		private String userId;
		private List<MetricEntry> metrics;
	}

	@Getter
	public static class SearchReq {
		private List<String> experiments;
		private List<String> metrics;
	}

	@Getter
	public static class SearchRes {
		private String workloadName;
		private List<MetricEntry> value;
	}

	@Getter
	public static class Res {
		private String workloadName;
		private Integer step;
		private Integer epochs;
		private Double wallTime;
		private Double relativeTime;
		private Map<String, Double> log;
	}

	@Getter
	@AllArgsConstructor
	public static class MetricEntry {
		private Integer step;
		private Integer epochs;
		private Double wallTime;
		private Double relativeTime;
		private Map<String, Double> metrics;
	}
}
