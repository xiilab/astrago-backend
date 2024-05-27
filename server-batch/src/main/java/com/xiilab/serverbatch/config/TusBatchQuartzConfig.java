package com.xiilab.serverbatch.config;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
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
	private final Scheduler scheduler;

	@PostConstruct
	protected void jobProcess() throws SchedulerException {
		tusScheduler();
	}

	private void tusScheduler() throws SchedulerException {
		JobDataMap jobDataMap = new JobDataMap();
		JobDetail tusCleanUpJobDetail = createJobDetail(TusCleanUpJob.class, "tusCleanUpJob", jobDataMap);
		Trigger tusCleanUpJobTrigger = createCronTrigger(tusCleanUpJobDetail, "tusCleanUpJobTrigger", "* 0/5 * * * ?"); //매일 새벽 1시
		scheduler.scheduleJob(tusCleanUpJobDetail, tusCleanUpJobTrigger);
	}
}
