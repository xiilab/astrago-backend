package com.xiilab.serverbatch.dto;

import org.quartz.JobDataMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceOptimizationDTO {
	private int cpu;
	private int mem;
	private int gpu;
	private int hour;

	public JobDataMap convertToJobDataMap() {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("cpu", cpu);
		jobDataMap.put("mem", mem);
		jobDataMap.put("gpu", gpu);
		jobDataMap.put("hour", hour);
		return jobDataMap;
	}

	public ResourceOptimizationDTO(JobDataMap jobDataMap) {
		this.cpu = (int)jobDataMap.get("cpu");
		this.mem = (int)jobDataMap.get("mem");
		this.gpu = (int)jobDataMap.get("gpu");
		this.hour = (int)jobDataMap.get("hour");
	}
}
