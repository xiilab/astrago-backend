package com.xiilab.serverbatch.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class BatchResourceOptimizationJob implements Job {
	@Override
	public void execute(JobExecutionContext context) {
		log.info("batch resource optimization job start....");

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		int cpuLimit = (int)jobDataMap.get("cpu");
		int memLimit = (int)jobDataMap.get("mem");
		int gpuLimit = (int)jobDataMap.get("gpu");
		int hour = (int)jobDataMap.get("hour");

		log.info("cpuLimit : {}", cpuLimit);
		log.info("memLimit : {}", memLimit);
		log.info("gpuLimit : {}", gpuLimit);
		log.info("hour : {}", hour);
	}
}
