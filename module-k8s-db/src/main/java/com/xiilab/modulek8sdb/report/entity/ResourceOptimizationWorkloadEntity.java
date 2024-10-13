package com.xiilab.modulek8sdb.report.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "TB_RESOURCE_OPITMIZATION_WORKLOAD")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceOptimizationWorkloadEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "WORKLOAD_RESOURCE_NAME")
	private String workloadResourceName;
	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "CPU_USAGE")
	private float cpuUsage;
	@Column(name = "MEM_USAGE")
	private float memUsage;
	@Column(name = "GPU_USAGE")
	private float gpuUsage;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private ResourceOptimizationJobEntity resourceOptimizationJob;
}
