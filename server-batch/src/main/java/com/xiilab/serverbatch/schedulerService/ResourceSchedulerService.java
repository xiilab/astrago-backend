package com.xiilab.serverbatch.schedulerService;

import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.springframework.stereotype.Service;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.dto.ResourceOptimizationDTO;
import com.xiilab.serverbatch.dto.ResourceOptimizerStatus;
import com.xiilab.serverbatch.job.BatchResourceOptimizationJob;
import com.xiilab.serverbatch.job.InteractiveResourceOptimizationJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceSchedulerService {
	private final Scheduler scheduler;
	private static final String ASTRA = "astra";

	public void registerResourceScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob) throws
		SchedulerException {
		JobDetail job = createJob(batchJob, optimizationDTO);
		Trigger trigger = createTrigger(job);
		scheduler.scheduleJob(job, trigger);
	}

	public void updateResourceOptimizationScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob) throws
		SchedulerException {
		stopResourceScheduler(batchJob);
		registerResourceScheduler(optimizationDTO, batchJob);
	}

	public void stopResourceScheduler(BatchJob batchJob) throws SchedulerException {
		Set<JobKey> astra = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		astra.stream().filter(jobKey -> jobKey.getName().equals(batchJob.name())).findFirst().ifPresent(job -> {
			try {
				scheduler.deleteJob(job);
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public ResourceOptimizerStatus getResourceOptimizerStatus() throws SchedulerException {
		Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		Set<String> jobNameSet = jobSet.stream().map(Key::getName).collect(Collectors.toSet());
		boolean interactive = jobNameSet.contains(BatchJob.INTERACTIVEJOBOPTIMIZATION.name());
		boolean batch = jobNameSet.contains(BatchJob.BATCHJOBOPTIMIZATION.name());
		return new ResourceOptimizerStatus(batch, interactive);
	}

	private JobDetail createJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
		if (jobType == BatchJob.BATCHJOBOPTIMIZATION) {
			return JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.BATCHJOBOPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.build();
		} else {
			return JobBuilder.newJob(InteractiveResourceOptimizationJob.class)
				.withIdentity(BatchJob.INTERACTIVEJOBOPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.build();
		}
	}

	private Trigger createTrigger(JobDetail job) {
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
			.startNow()
			.forJob(job)
			.build();
	}
}
