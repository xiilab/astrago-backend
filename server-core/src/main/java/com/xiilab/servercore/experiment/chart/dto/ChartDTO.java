package com.xiilab.servercore.experiment.chart.dto;

import java.util.List;

import com.xiilab.servercore.experiment.chart.entity.ChartEntity;
import com.xiilab.servercore.experiment.chart.entity.PanelEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ChartDTO {
	@Getter
	@AllArgsConstructor
	public static class Req {
		@NotBlank
		private String title;
		@NotBlank
		private String xAxis;
		private Double xMin;
		private Double xMax;
		@NotNull
		private List<String> yAxis;
		private Double yMin;
		private Double yMax;
	}

	@Getter
	public static class Res {
		private final Long id;
		private final String title;
		private final String xAxis;
		private final Double xAxisMin;
		private final Double xAxisMax;
		private final List<String> yAxis;
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
