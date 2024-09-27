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
	private boolean running;
	private boolean andYN;

	public ResourceOptimizationDTO(JobDataMap jobDataMap, boolean isRunning) {
		this.cpu = (int)jobDataMap.get("cpu");
		this.mem = (int)jobDataMap.get("mem");
		this.gpu = (int)jobDataMap.get("gpu");
		this.hour = (int)jobDataMap.get("hour");
		this.running = isRunning;
		this.andYN = (boolean)jobDataMap.get("andYN");
	}

	public JobDataMap convertToJobDataMap() {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("cpu", cpu);
		jobDataMap.put("mem", mem);
		jobDataMap.put("gpu", gpu);
		jobDataMap.put("hour", hour);
		jobDataMap.put("andYN", andYN);
		return jobDataMap;
	}
}
