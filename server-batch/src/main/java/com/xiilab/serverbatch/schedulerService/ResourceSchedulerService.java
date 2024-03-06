package com.xiilab.serverbatch.schedulerService;

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

	public void registerResourceScheduler(ResourceOptimizationDTO optimizationDTO, BatchJob batchJob)
		throws Exception {
		if (optimizationDTO.getHour() < 5 || optimizationDTO.getHour() > 24) {
			throw new Exception(WORKLOAD_OPTIMIZATION_HOUR_INPUT_ERROR.getMessage());
		}
		//resource optimization job, trigger 생성
		JobDetail resourceOptimizationJob = createResourceOptimizationJob(batchJob, optimizationDTO);
		Trigger optimizationTrigger = createTrigger(resourceOptimizationJob, optimizationDTO.getHour());
		//생성된 job과 trigger을 scheduler에 등록
		scheduler.scheduleJob(resourceOptimizationJob, optimizationTrigger);
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
		ResourceOptimizationDTO batchOptimizationStatus = null;
		ResourceOptimizationDTO interactiveOptimizationStatus = null;
		for (JobKey jobKey : jobSet) {
			String name = jobKey.getName();
			if (name.equals(BatchJob.BATCH_JOB_OPTIMIZATION.name())) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				batchOptimizationStatus = new ResourceOptimizationDTO(jobDetail.getJobDataMap());
			} else if (name.equals(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name())) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				interactiveOptimizationStatus = new ResourceOptimizationDTO(jobDetail.getJobDataMap());
			}
		}
		return new ResourceOptimizerStatus(batchOptimizationStatus, interactiveOptimizationStatus);
	}

	private JobDetail createResourceOptimizationJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
		if (jobType == BatchJob.BATCH_JOB_OPTIMIZATION) {
			return JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.BATCH_JOB_OPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.convertToJobDataMap())
				.build();
		} else {
			return JobBuilder.newJob(InteractiveResourceOptimizationJob.class)
				.withIdentity(BatchJob.INTERACTIVE_JOB_OPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.convertToJobDataMap())
				.build();
		}
	}

	private Trigger createTrigger(JobDetail job, int hour) {
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(String.format("0 0 0/1 * * ?", hour)))
			.startNow()
			.forJob(job)
			.build();
	}
}
