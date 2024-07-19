package com.xiilab.modulek8sdb.preset.entity;

import java.math.BigDecimal;

import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_RESOURCE_PRESET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResourcePresetEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RESOURCE_PRESET_ID")
	private Long id;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "LAUNCHER_CPU_USAGE")
	private BigDecimal launcherCpuUsage;

	@Column(name = "LAUNCHER_MEM_USAGE")
	private BigDecimal launcherMemUsage;

	@Column(name = "GPU_USAGE")
	private Integer gpuUsage;

	@Column(name = "CPU_USAGE", precision = 5, scale = 1)
	private BigDecimal cpuUsage;

	@Column(name = "MEM_USAGE", precision = 5, scale = 1)
	private BigDecimal memUsage;

	@Column(name = "NODE_TYPE")
	@Enumerated(EnumType.STRING)
	private NodeType nodeType;

	@Builder(builderClassName = "SaveResourcePresetBuilder", builderMethodName = "saveResourcePresetBuilder")
	public ResourcePresetEntity(String title, String description, BigDecimal launcherCpuUsage, BigDecimal launcherMemUsage,
		Integer gpuUsage, BigDecimal cpuUsage,
		BigDecimal memUsage, NodeType nodeType) {
		this.title = title;
		this.description = description;
		this.launcherCpuUsage = launcherCpuUsage;
		this.launcherMemUsage = launcherMemUsage;
		this.gpuUsage = gpuUsage;
		this.cpuUsage = cpuUsage;
		this.memUsage = memUsage;
		this.nodeType = nodeType;
	}

	@Builder(builderClassName = "UpdateResourcePresetBuilder", builderMethodName = "updateResourcePresetBuilder")
	public ResourcePresetEntity(Long id, String title, BigDecimal launcherMemUsage, BigDecimal launcherCpuUsage,
		String description, Integer gpuUsage, BigDecimal cpuUsage,
		BigDecimal memUsage, NodeType nodeType) {
		this.id = id;
		this.title = title;
		this.launcherCpuUsage = launcherCpuUsage;
		this.launcherMemUsage = launcherMemUsage;
		this.description = description;
		this.gpuUsage = gpuUsage;
		this.cpuUsage = cpuUsage;
		this.memUsage = memUsage;
		this.nodeType = nodeType;
	}
}
