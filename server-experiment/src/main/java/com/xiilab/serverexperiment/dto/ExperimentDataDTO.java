package com.xiilab.serverexperiment.dto;

import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ExperimentDataDTO {

	@Getter
	public static class TrainReq {
		private String uuid;
		private String workloadName;
		private String userId;
		private List<MetricEntry> metrics;
	}

	@Getter
	public static class SystemReq {
		private String uuid;
		private String workloadName;
		private String userId;
		private Double wallTime;
		private Double cpuUsage;
		private Double memUsage;
		private List<GPUInfo> gpuInfos;
	}

	@Getter
	public static class GPUInfo {
		private String name;
		private Double gpuUsage;
		private Double memUsage;
		private Double temperature;
	}

	@Getter
	public static class SearchReq {
		@NotEmpty
		private List<@NotNull String> experiments;
		@NotEmpty
		private List<@NotNull String> metrics;
	}

	@Getter
	public static class SearchRes {
		private String uuid;
		private List<MetricEntry> value;
	}

	@Getter
	public static class SystemSearchRes {
		private String uuid;
		private List<ResourceEntry> value;
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

	@Getter
	@AllArgsConstructor
	public static class ResourceEntry {
		private Double cpuUsage;
		private Double memUsage;
		private List<GPUResourceEntry> gpuInfos;
		private Double wallTime;
	}

	@Getter
	@AllArgsConstructor
	public static class GPUResourceEntry {
		private String name;
		private Double gpuUsage;
		private Double memUsage;
		private Double temperature;
	}

	@Getter
	@Builder
	public static class TableDTO {
		private String id;
		private String name;
		private String resourceName;
		private WorkloadStatus status;
		private String userName;
		private List<ExperimentQueryResult.LabelDTO> labels;
		private MetricEntry metricEntry;
	}
}
