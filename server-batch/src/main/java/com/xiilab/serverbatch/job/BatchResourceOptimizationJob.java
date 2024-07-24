package com.xiilab.serverbatch.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class BatchResourceOptimizationJob extends QuartzJobBean {
	@Autowired
	private WorkloadModuleService workloadModuleService;
	@Autowired
	private PrometheusService prometheusService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("batch resource optimization alert job start....");

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		int cpuLimit = (int)jobDataMap.get("cpu");
		int memLimit = (int)jobDataMap.get("mem");
		int gpuLimit = (int)jobDataMap.get("gpu");
		int hour = (int)jobDataMap.get("hour");
		log.info("cpuLimit : {}", cpuLimit);
		log.info("memLimit : {}", memLimit);
		log.info("gpuLimit : {}", gpuLimit);
		log.info("hour : {}", hour);

		List<ResponseDTO.RealTimeDTO> optimizationList = getOverResourcePodList(cpuLimit, memLimit, gpuLimit, hour);
		List<ResponseDTO.RealTimeDTO> alarmList = getOverResourcePodList(cpuLimit, memLimit, gpuLimit, hour - 1);

		//최적화 대상에 대한 distinct 처리 진행
		List<ResourceOptimizationTargetDTO> alarmDistinctList = alarmList.stream()
			.map(realTimeDTO -> new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName()))
			.distinct()
			.toList();

		log.info("over resource total pod list count : {}", alarmDistinctList.size());

		List<AbstractModuleWorkloadResDTO> alarmParentList = workloadModuleService.getParentControllerList(
			alarmDistinctList);


		//최적화 대상에 대한 distinct 처리 진행
		List<ResourceOptimizationTargetDTO> optimizationDistinctList = optimizationList.stream()
			.map(realTimeDTO -> new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName()))
			.distinct()
			.toList();

		log.info("over resource total pod list count : {}", optimizationDistinctList.size());
		List<AbstractModuleWorkloadResDTO> parentControllerList = workloadModuleService.getParentControllerList(
			optimizationDistinctList);
		int resultCnt = workloadModuleService.optimizationInteractiveWorkload(optimizationDistinctList);

		log.info("자원회수된 workload의 개수 : {}", resultCnt);

	}

	private List<ResponseDTO.RealTimeDTO> getOverResourcePodList(int cpu, int mem, int gpu, int hour) {
		//통합을 위한 리스트 생성
		List<ResponseDTO.RealTimeDTO> totalList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now().minusHours(hour);
		String unixTimeStamp = String.valueOf(now.atZone(ZoneId.systemDefault()).toEpochSecond());

		//prometheus에서 기준치 이상을 넘은 워크로드 조회
		List<ResponseDTO.RealTimeDTO> overResourceCPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_CPU, String.valueOf(hour), String.valueOf(cpu), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> overResourceGPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_GPU, String.valueOf(hour), String.valueOf(gpu), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> overResourceMEMPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_MEM, String.valueOf(hour), String.valueOf(mem), unixTimeStamp);
		log.info("over resource cpu pod list count : {}", overResourceCPUPodList.size());
		log.info("over resource gpu pod list count : {}", overResourceGPUPodList.size());
		log.info("over resource mem pod list count : {}", overResourceMEMPodList.size());

		totalList.addAll(overResourceCPUPodList);
		totalList.addAll(overResourceGPUPodList);
		totalList.addAll(overResourceMEMPodList);

		return totalList;
	}
}
