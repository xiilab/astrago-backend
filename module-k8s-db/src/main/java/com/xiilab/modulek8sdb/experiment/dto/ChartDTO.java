package com.xiilab.modulek8sdb.experiment.dto;

import java.util.List;

import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.experiment.entity.PanelEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ChartDTO {
	@Getter
	@AllArgsConstructor
	public enum MetricType {
		CUSTOM,
		SYSTEM
	}

	@Getter
	@AllArgsConstructor
	public static class Req {
		private String title;
		private String xAxis;
		private Double xMin;
		private Double xMax;
		private List<YAxis> yAxis;
		private Double yMin;
		private Double yMax;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class YAxis {
		private String metric;
		private MetricType type;
	}

	@Getter
	public static class Res {
		private final Long id;
		private final String title;
		private final String xAxis;
		private final Double xAxisMin;
		private final Double xAxisMax;
		private final List<YAxis> yAxis;
		private final Double yAxisMin;
		private final Double yAxisMax;

		public Res(ChartEntity chartEntity) {
			this.id = chartEntity.getId();
			this.title = chartEntity.getTitle();
			this.xAxis = chartEntity.getXAxis();
			this.xAxisMin = chartEntity.getXAxisMin();
			this.xAxisMax = chartEntity.getXAxisMax();
			this.yAxis = chartEntity.getYAxis();
			this.yAxisMin = chartEntity.getYAxisMin();
			this.yAxisMax = chartEntity.getYAxisMax();
		}
	}

	@Getter
	public static class Panel {
		private final Long id;
		private final String title;

		public Panel(PanelEntity panelEntity) {
			this.id = panelEntity.getId();
			this.title = panelEntity.getTitle();
		}
	}
}
