package com.xiilab.serverbatch.schedulerService;

import static com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode.*;

import java.util.List;
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
import com.xiilab.serverbatch.job.BatchOptimizationAlertJob;
import com.xiilab.serverbatch.job.BatchResourceOptimizationJob;
import com.xiilab.serverbatch.job.InteractiveOptimizationAlertJob;
import com.xiilab.serverbatch.job.InteractiveResourceOptimizationJob;
import com.xiilab.serverbatch.job.OptimizationJobListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceSchedulerService {
	private final Scheduler scheduler;

	public ResourceSchedulerService(Scheduler scheduler) throws SchedulerException {
		this.scheduler = scheduler;
		scheduler.getListenerManager().addJobListener(new OptimizationJobListener(scheduler));
	}

	private static final String ASTRA = "astra";

	public void registerResourceScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob) throws
		Exception {
		if (optimizationDTO.getHour() < 2) {
			throw new Exception(WORKLOAD_OPTIMIZATION_HOUR_INPUT_ERROR.getMessage());
		}
		//resource optimization alert job, trigger 생성
		JobDetail resourceOptimizationAlertJob = createResourceOptimizationAlertJob(batchJob, optimizationDTO);
		Trigger optimizationAlertTrigger = createTrigger(resourceOptimizationAlertJob, optimizationDTO.getHour() - 1);
		//생성된 job과 trigger을 scheduler에 등록
		scheduler.scheduleJob(resourceOptimizationAlertJob, optimizationAlertTrigger);
	}

	public void updateResourceOptimizationScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob) throws
		Exception {
		stopResourceScheduler(batchJob);
		registerResourceScheduler(optimizationDTO, batchJob);
	}

	public void stopResourceScheduler(BatchJob batchJob) throws SchedulerException {
		Set<JobKey> astra = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		List<JobKey> list = astra.stream().filter(jobKey -> jobKey.getName().contains(batchJob.name())).toList();
		scheduler.deleteJobs(list);
	}

	public ResourceOptimizerStatus getResourceOptimizerStatus() throws SchedulerException {
		Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		Set<String> jobNameSet = jobSet.stream().map(Key::getName).collect(Collectors.toSet());
		boolean interactive = jobNameSet.contains(BatchJob.INTERACTIVEJOBOPTIMIZATION.name());
		boolean batch = jobNameSet.contains(BatchJob.BATCHJOBOPTIMIZATION.name());
		return new ResourceOptimizerStatus(batch, interactive);
	}

	private JobDetail createResourceOptimizationJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
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

	private JobDetail createResourceOptimizationAlertJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
		if (jobType == BatchJob.BATCHJOBOPTIMIZATION) {
			return JobBuilder.newJob(BatchOptimizationAlertJob.class)
				.withIdentity(BatchJob.BATCHJOBOPTIMIZATION.name() + "ALERT", ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.build();
		} else {
			return JobBuilder.newJob(InteractiveOptimizationAlertJob.class)
				.withIdentity(BatchJob.INTERACTIVEJOBOPTIMIZATION.name() + "ALERT", ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.build();
		}
	}

	private Trigger createTrigger(JobDetail job, int hour) {
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(String.format("0 0/%s * * * ?", hour)))
			.startNow()
			.forJob(job)
			.build();
	}
}
