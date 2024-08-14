package com.xiilab.serverexperiment.dto;

import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
		@NotEmpty
		private List<@NotNull String> experiments;
		@NotEmpty
		private List<@NotNull String> metrics;
	}

	@Getter
	public static class ChartRes {
		private final String title;
		private final String xAxis;
		private final Double xAxisMin;
		private final Double xAxisMax;
		private final List<String> yAxis;
		private final Double yAxisMin;
		private final Double yAxisMax;
		// private final List<SearchRes> chartList;

		public ChartRes(ChartEntity chartEntity) {
			this.title = chartEntity.getTitle();
			this.xAxis = chartEntity.getXAxis();
			this.xAxisMin = chartEntity.getXAxisMin();
			this.xAxisMax = chartEntity.getXAxisMax();
			this.yAxis = chartEntity.getYAxis();
			this.yAxisMin = chartEntity.getYAxisMin();
			this.yAxisMax = chartEntity.getYAxisMax();
			// this.chartList = searchRes;
		}
	}

	@Getter
	public static class SearchRes {
		private String uuid;
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
