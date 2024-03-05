package com.xiilab.serverbatch.job;

import static org.quartz.TriggerBuilder.*;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.dto.ResourceOptimizationDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OptimizationJobListener implements JobListener {
	private static final String ASTRA = "astra";
	private final Scheduler scheduler;

	@Override
	public String getName() {
		return OptimizationJobListener.class.getName();
	}

	/**
	 * Job 수행되기 전 상태
	 * 해당 listener를 활용하여, 예측 후 한시간 뒤에 최적화 job이 실행될 수 있도록 한다.
	 *
	 * @param jobExecutionContext
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
		String jobKey = jobExecutionContext.getJobDetail().getKey().getName();
		if (jobKey.equals(BatchJob.INTERACTIVEJOBOPTIMIZATION.name() + "ALERT") ||
			jobKey.equals(BatchJob.BATCHJOBOPTIMIZATION.name() + "ALERT")) {
			log.info("최적화 job 생성 시작");
			JobDetail jobDetail = jobExecutionContext.getJobDetail();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			int cpu = (int)jobDataMap.get("cpu");
			int mem = (int)jobDataMap.get("mem");
			int gpu = (int)jobDataMap.get("gpu");
			int hour = (int)jobDataMap.get("hour");
			ResourceOptimizationDTO resourceOptimizationDTO = new ResourceOptimizationDTO(cpu, mem, gpu, hour);
			String name = jobDetail.getKey().getName();
			JobDetail job = null;
			//최적화 타입에 따른 분기 처리 진행
			if (name.contains(BatchJob.INTERACTIVEJOBOPTIMIZATION.name())) {
				job = createResourceOptimizationJob(BatchJob.INTERACTIVEJOBOPTIMIZATION, resourceOptimizationDTO);
			} else if (name.contains(BatchJob.BATCHJOBOPTIMIZATION.name())) {
				job = createResourceOptimizationJob(BatchJob.BATCHJOBOPTIMIZATION, resourceOptimizationDTO);
			}
			Trigger trigger = create1HourTrigger(job);
			try {
				scheduler.scheduleJob(job, trigger);
				log.info("최적화 job 생성 완료");
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Job이 중단된 상태
	 *
	 * @param jobExecutionContext
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

	}

	/**
	 * Job 수행이 완료된 상태
	 *
	 * @param jobExecutionContext
	 * @param e
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		String jobKey = key.getName();
		if (jobKey.equals(BatchJob.INTERACTIVEJOBOPTIMIZATION.name()) || jobKey.equals(BatchJob.BATCHJOBOPTIMIZATION.name())) {
			try {
				scheduler.deleteJob(key);
			} catch (SchedulerException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private JobDetail createResourceOptimizationJob(BatchJob jobType, ResourceOptimizationDTO optimizationDTO) {
		if (jobType == BatchJob.BATCHJOBOPTIMIZATION) {
			return JobBuilder.newJob(BatchResourceOptimizationJob.class)
				.withIdentity(BatchJob.BATCHJOBOPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.storeDurably(false)
				.build();
		} else {
			return JobBuilder.newJob(InteractiveResourceOptimizationJob.class)
				.withIdentity(BatchJob.INTERACTIVEJOBOPTIMIZATION.name(), ASTRA)
				.usingJobData(optimizationDTO.getJobDataMap())
				.storeDurably(false)
				.build();
		}
	}

	private Trigger create1HourTrigger(JobDetail job) {
		return newTrigger()
			.forJob(job)
			.startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.HOUR))
			.build();
	}
}
