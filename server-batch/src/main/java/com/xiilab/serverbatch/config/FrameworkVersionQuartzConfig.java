package com.xiilab.serverbatch.config;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;

import com.xiilab.serverbatch.job.FrameworkVersionJob;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FrameworkVersionQuartzConfig extends CommonQuartzConfig {
	private final Scheduler scheduler;

	@PostConstruct
	protected void jobProcess() throws SchedulerException {
		cronScheduler();
	}

	private void cronScheduler() throws SchedulerException {
		JobDataMap jobDataMap = new JobDataMap();
		JobDetail frameworkVersionJobDetail = createJobDetail(FrameworkVersionJob.class, "FrameworkVersionJob", jobDataMap);
		Trigger frameworkVersionJobTrigger = createCronTrigger(frameworkVersionJobDetail, "frameworkVersionJobTrigger", "0 0 2 * * ?"); //매일 새벽 2시
		scheduler.scheduleJob(frameworkVersionJobDetail, frameworkVersionJobTrigger);
	}
}
