package com.xiilab.serverbatch.service;

import static com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode.*;

import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class ResourceOptimizationSchedulerService {
	private final Scheduler scheduler;
	private static final String ASTRA = "astrago";

	public void registerResourceScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob)
		throws SchedulerException {
		if (optimizationDTO.getHour() < 5 || optimizationDTO.getHour() > 24) {
			throw new IllegalArgumentException(WORKLOAD_OPTIMIZATION_HOUR_INPUT_ERROR.getMessage());
		}
		if (optimizationDTO.isRunning()) {
			//resource optimization job, trigger 생성
			JobDetail resourceOptimizationJob = createResourceOptimizationJob(batchJob, optimizationDTO);
			Trigger optimizationTrigger = createTrigger(resourceOptimizationJob, optimizationDTO.getHour());
			//생성된 job과 trigger을 scheduler에 등록
			scheduler.scheduleJob(resourceOptimizationJob, optimizationTrigger);
		}
	}

	/**
	 * quartz job을 등록하고 수정하는 메소드
	 * 해당 메소드에서는 기존의 job이 존재할 경우, 수정하는 방식이 아닌, 삭제 후 재생성 하는 방식으로 진행한다.
	 *
	 * @param optimizationDTO 생성 or 수정 할 job에 대한 info dto
	 * @param batchJob        batch, interactive type
	 * @throws Exception
	 */
	public void updateResourceOptimizationScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob) throws
		Exception {
		if (optimizationDTO.isRunning()) {
			//job을 바로 실행하는 경우기에 job, trigger를 등록한다.
			stopResourceScheduler(batchJob);
			registerResourceScheduler(optimizationDTO, batchJob);
		} else {
			//job을 바로 실행하는 것이 아닌 경우 job만 생성하고 trigger는 생성하지 않는다.
			stopResourceScheduler(batchJob);
			JobDetail resourceOptimizationJob = createResourceOptimizationJob(batchJob, optimizationDTO);
			scheduler.addJob(resourceOptimizationJob, true);
		}
	}

	public void stopResourceScheduler(BatchJob batchJob) throws SchedulerException {
		Set<JobKey> astra = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		List<JobKey> list = astra.stream().filter(jobKey -> jobKey.getName().contains(batchJob.name())).toList();
		scheduler.deleteJobs(list);
	}

	public ResourceOptimizerStatus getResourceSchedulerStatus() throws SchedulerException {
		return getResourceOptimizerStatus();
	}

	private ResourceOptimizerStatus getResourceOptimizerStatus() throws SchedulerException {
		Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));
		ResourceOptimizationDTO batchOptimizationStatus = null;
		ResourceOptimizationDTO interactiveOptimizationStatus = null;
		for (JobKey jobKey : jobSet) {
			String name = jobKey.getName();
			if (name.equals(BatchJob.BATCH_JOB_OPTIMIZATION.name())) {
				boolean jobRunning = isJobRunning(jobKey);
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				batchOptimizationStatus = new ResourceOptimizationDTO(jobDetail.getJobDataMap(), jobRunning);
			} else if (name.equals(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name())) {
				boolean jobRunning = isJobRunning(jobKey);
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				interactiveOptimizationStatus = new ResourceOptimizationDTO(jobDetail.getJobDataMap(), jobRunning);
			}
		}
		return new ResourceOptimizerStatus(batchOptimizationStatus, interactiveOptimizationStatus);
	}

	private JobDetail createResourceOptimizationJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
		if (jobType == BatchJob.BATCH_JOB_OPTIMIZATION) {
			return JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.BATCH_JOB_OPTIMIZATION.name(), ASTRA)
				.storeDurably(true)
				.usingJobData(optimizationDTO.convertToJobDataMap())
				.build();
		} else {
			return JobBuilder.newJob(InteractiveResourceOptimizationJob.class)
				.withIdentity(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name(), ASTRA)
				.storeDurably(true)
				.usingJobData(optimizationDTO.convertToJobDataMap())
				.build();
		}
	}

	private boolean isJobRunning(JobKey jobKey) throws SchedulerException {
		List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
		if (!CollectionUtils.isEmpty(triggersOfJob)) {
			Trigger trigger = triggersOfJob.get(0);
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			return switch (triggerState) {
				case PAUSED, BLOCKED, ERROR, COMPLETE, NONE -> false;
				case NORMAL -> true;
			};
		} else {
			return false;
		}
	}

	private Trigger createTrigger(JobDetail job, int hour) {
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(getCronExpression(hour)))
			.startNow()
			.forJob(job)
			.build();
	}

	private String getCronExpression(int hour) {
		if (hour == 24) {
			return "0 0 0 1 * ?";
		} else {
			return String.format("0 0 0/%s * * ?", hour);
		}
	}
}
