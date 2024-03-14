package com.xiilab.serverbatch.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;

import com.xiilab.serverbatch.job.FrameworkVersionJob;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FrameworkVersionQuartzConfig {
	private final Scheduler scheduler;

	@PostConstruct
	private void jobProcess() throws SchedulerException {
		cronScheduler();
	}

	private JobDetail createJobDetail(Class<? extends Job> jobClass, String jobName, JobDataMap jobDataMap) {
		JobDetail jobDetail = JobBuilder.newJob(jobClass)
			.withIdentity(jobName)
			.setJobData(jobDataMap)
			.storeDurably()
			.build();
		return jobDetail;
	}

	private Trigger createCronTrigger(JobDetail jobDetail, String triggerName, String cronExpression) {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		CronTrigger trigger = TriggerBuilder.newTrigger()
			.forJob(jobDetail)
			.withIdentity(triggerName)
			.withSchedule(cronScheduleBuilder)
			.build();
		return trigger;
	}
	private void cronScheduler() throws SchedulerException {
		JobDataMap jobDataMap = new JobDataMap();
		JobDetail frameworkVersionJobDetail = createJobDetail(FrameworkVersionJob.class, "FrameworkVersionJob", jobDataMap);

		// Trigger frameworkVersionJobTrigger = createCronTrigger(frameworkVersionJobDetail, "frameworkVersionJobTrigger", "0/30 * * * * ?");
		Trigger frameworkVersionJobTrigger = createCronTrigger(frameworkVersionJobDetail, "frameworkVersionJobTrigger", "1 * * * * ?");
		scheduler.scheduleJob(frameworkVersionJobDetail, frameworkVersionJobTrigger);
	}

}
