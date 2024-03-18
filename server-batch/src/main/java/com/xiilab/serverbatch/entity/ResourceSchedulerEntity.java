package com.xiilab.serverbatch.entity;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.dto.ResourceOptimizationDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_RESOURCE_SCHEDULER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ResourceSchedulerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	private BatchJob jobType;
	private int cpu;
	private int mem;
	private int gpu;
	private int hour;
	private boolean running;

	public void updateValue(ResourceOptimizationDTO optimizationDTO) {
		this.cpu = optimizationDTO.getCpu();
		this.mem = optimizationDTO.getMem();
		this.gpu = optimizationDTO.getGpu();
		this.hour = optimizationDTO.getHour();
		this.running = optimizationDTO.isRunning();
	}
}
