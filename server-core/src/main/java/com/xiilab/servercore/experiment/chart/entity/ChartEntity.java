package com.xiilab.servercore.experiment.chart.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.servercore.experiment.chart.dto.ChartDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_CHART")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChartEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "X_AXIS")
	private String xAxis;
	@Column(name = "X_AXIS_MIN")
	private Double xAxisMin;
	@Column(name = "X_AXIS_MAX")
	private Double xAxisMax;
	@Column(name = "Y_AXIS")
	private String yAxis;
	@Column(name = "Y_AXIS_MIN")
	private Double yAxisMin;
	@Column(name = "Y_AXIS_MAX")
	private Double yAxisMax;
	@ManyToOne(fetch = FetchType.LAZY)
	private PanelEntity panel;

	public ChartEntity(ChartDTO.Req req, PanelEntity panel) {
		this.title = req.getTitle();
		this.xAxis = req.getXAxis();
		this.xAxisMin = req.getXMin();
		this.xAxisMax = req.getXMax();
		this.yAxis = req.getYAxis();
		this.yAxisMin = req.getYMin();
		this.yAxisMax = req.getYMax();
		this.panel = panel;
	}

	public void updateChart(ChartDTO.Req req) {
		this.title = req.getTitle();
		this.xAxis = req.getXAxis();
		this.xAxisMin = req.getXMin();
		this.xAxisMax = req.getXMax();
		this.yAxis = req.getYAxis();
		this.yAxisMin = req.getYMin();
		this.yAxisMax = req.getYMax();
	}
}