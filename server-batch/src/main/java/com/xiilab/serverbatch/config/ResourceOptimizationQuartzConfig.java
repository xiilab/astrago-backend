package com.xiilab.serverbatch.config;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.job.BatchResourceOptimizationJob;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ResourceOptimizationQuartzConfig extends CommonQuartzConfig {
	private static final String ASTRAGO = "astrago";
	private final Scheduler scheduler;

	@PostConstruct
	protected void jobProcess() throws SchedulerException {
		initBatchResourceOptimizationJob();
		initInteractiveResourceOptimizationJob();
	}

	private void initBatchResourceOptimizationJob() throws SchedulerException {
		JobDetail frameworkVersionJob = scheduler.getJobDetail(
			new JobKey(BatchJob.BATCH_JOB_OPTIMIZATION.name(), ASTRAGO));
		if (frameworkVersionJob == null) {
			JobDetail job = JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.BATCH_JOB_OPTIMIZATION.name(), ASTRAGO)
				.storeDurably(true)
				.usingJobData(getInitJobDataMap())
				.build();
			scheduler.addJob(job, true);
		}
	}

	private void initInteractiveResourceOptimizationJob() throws SchedulerException {
		JobDetail frameworkVersionJob = scheduler.getJobDetail(
			new JobKey(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name(), ASTRAGO));
		if (frameworkVersionJob == null) {
			JobDetail job = JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name(), ASTRAGO)
				.storeDurably(true)
				.usingJobData(getInitJobDataMap())
				.build();
			scheduler.addJob(job, true);
		}
	}

	private JobDataMap getInitJobDataMap() {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("cpu", 5);
		jobDataMap.put("mem", 5);
		jobDataMap.put("gpu", 5);
		jobDataMap.put("hour", 5);
		jobDataMap.put("andYN", false);
		return jobDataMap;
	}
}
