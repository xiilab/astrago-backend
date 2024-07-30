package com.xiilab.servercore.chart.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_CHART_PANEL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PanelEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "TITLE")
	private String title;
	@OneToMany(mappedBy = "panel", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ChartEntity> chartList = new ArrayList<>();

	public void updateTitle(String title) {
		this.title = title;
	}

}
