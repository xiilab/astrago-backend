package com.xiilab.serverbatch.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public abstract class CommonQuartzConfig {
	protected abstract void jobProcess() throws SchedulerException;

	protected JobDetail createJobDetail(Class<? extends Job> jobClass, String jobName, JobDataMap jobDataMap) {
		return JobBuilder.newJob(jobClass)
			.withIdentity(jobName)
			.setJobData(jobDataMap)
			.storeDurably()
			.build();
	}

	protected Trigger createCronTrigger(JobDetail jobDetail, String triggerName, String cronExpression) {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		return TriggerBuilder.newTrigger()
			.forJob(jobDetail)
			.withIdentity(triggerName)
			.withSchedule(cronScheduleBuilder)
			.build();
	}
}
