package com.xiilab.serverbatch.config;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.xiilab.serverbatch.job.TusCleanUpJob;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TusBatchQuartzConfig extends CommonQuartzConfig {
	private static final String JOB_NAME = "tusCleanUpJob";
	private final Scheduler scheduler;

	@PostConstruct
	protected void jobProcess() throws SchedulerException {
		tusScheduler();
	}

	private void tusScheduler() throws SchedulerException {
		JobDetail tusCleanUpJob = scheduler.getJobDetail(new JobKey(JOB_NAME));
		if (tusCleanUpJob == null) {
			JobDataMap jobDataMap = new JobDataMap();
			JobDetail tusCleanUpJobDetail = createJobDetail(TusCleanUpJob.class, JOB_NAME, jobDataMap);
			Trigger tusCleanUpJobTrigger = createCronTrigger(tusCleanUpJobDetail, "tusCleanUpJobTrigger",
				"0 0 1 * * ?"); //매일 새벽 1시
			scheduler.scheduleJob(tusCleanUpJobDetail, tusCleanUpJobTrigger);
		}
	}
}
