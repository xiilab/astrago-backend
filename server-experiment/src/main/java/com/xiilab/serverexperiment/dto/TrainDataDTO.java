package com.xiilab.serverexperiment.dto;

import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.WorkloadStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TrainDataDTO {

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
	public static class InfoRes {
		private String workloadName;
		private List<String> tags;
		private WorkloadStatus workloadStatus;
		private List<MetricEntry> metricEntries;
	}

	@Getter
	public static class Res {
		private String workloadName;
		private WorkloadStatus status;
		private MetricEntry metricEntry;

		public Res(String workloadName, WorkloadStatus status, int step, double wallTime, double relativeTime,
			Map<String, Double> log) {
			this.workloadName = workloadName;
			this.status = status;
			metricEntry = new MetricEntry(step, 0, wallTime, relativeTime, log);
		}
	}

	@Getter
	@AllArgsConstructor
	public static class MetricEntry {
		private int step;
		private int epochs;
		private double wallTime;
		private double relativeTime;
		private Map<String, Double> log;
	}
}
