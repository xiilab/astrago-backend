package com.xiilab.modulek8sdb.report.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

@Getter
@Builder
@Entity
@Table(name = "TB_RESOURCE_OPITMIZATION_JOB")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceOptimizationJobEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "CPU_LIMIT")
	private int cpuCondition;
	@Column(name = "MEM_CONDITION")
	private int memCondition;
	@Column(name = "GPU_CONDITION")
	private int gpuCondition;
	@Column(name = "AND_YN")
	private boolean andYn;
	@Column(name = "START_TIME")
	private LocalDateTime startTime;
	@Column(name = "HOUR")
	private int hour;
	@Column(name = "OPTIMIZATION_COUNT")
	private int optimizationCount;
	@OneToMany(mappedBy = "resourceOptimizationJob", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ResourceOptimizationWorkloadEntity> workloads = new ArrayList<>();

	public void addWorkloadInfo(List<ResourceOptimizationWorkloadEntity> workloads) {
		this.workloads = workloads;
	}
}
