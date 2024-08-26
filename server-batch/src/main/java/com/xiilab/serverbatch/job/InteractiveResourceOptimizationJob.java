package com.xiilab.serverbatch.job;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class InteractiveResourceOptimizationJob extends QuartzJobBean {
	@Autowired
	private WorkloadModuleService workloadModuleService;
	@Autowired
	private ResourceOptimizationJob resourceOptimizationJob;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("interactive resource optimization job start....");

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		int cpuLimit = (int)jobDataMap.get("cpu");
		int memLimit = (int)jobDataMap.get("mem");
		int gpuLimit = (int)jobDataMap.get("gpu");
		int hour = (int)jobDataMap.get("hour");
		boolean andYN = (boolean)jobDataMap.get("andYN");
		log.info("cpuLimit : {}", cpuLimit);
		log.info("memLimit : {}", memLimit);
		log.info("gpuLimit : {}", gpuLimit);
		log.info("hour : {}", hour);

		//prometheus에서 기준치 이상을 넘은 워크로드 조회
		List<ResourceOptimizationTargetDTO> optimizationDistinctList = resourceOptimizationJob.getUnderResourcePodList(
			cpuLimit, memLimit,
			gpuLimit, hour, andYN);
		List<ResourceOptimizationTargetDTO> alarmList = resourceOptimizationJob.getUnderResourcePodList(cpuLimit,
			memLimit,
			gpuLimit, hour - 1, andYN);

		List<AbstractModuleWorkloadResDTO> alarmParentList = workloadModuleService.getParentControllerList(
			alarmList);

		resourceOptimizationJob.sendMail(alarmParentList);

		int resultCnt = workloadModuleService.optimizationWorkload(optimizationDistinctList);

		log.info("자원회수된 workload의 개수 : {}", resultCnt);
	}
}
