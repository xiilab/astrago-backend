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
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class InteractiveResourceOptimizationJob extends QuartzJobBean {
	private ApplicationContext applicationContext;
	private WorkloadModuleService workloadModuleService;
	private PrometheusService prometheusService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			//quartz는 Spring bean 사용이 안되기에 수동으로 의존성을 주입한다.
			applicationContext = (ApplicationContext)context.getScheduler().getContext().get("applicationContext");
			workloadModuleService = applicationContext.getBean(WorkloadModuleService.class);
			prometheusService = applicationContext.getBean(PrometheusService.class);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		log.info("interactive resource optimization job start....");

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		int cpuLimit = (int)jobDataMap.get("cpu");
		int memLimit = (int)jobDataMap.get("mem");
		int gpuLimit = (int)jobDataMap.get("gpu");
		int hour = (int)jobDataMap.get("hour");
		log.info("cpuLimit : {}", cpuLimit);
		log.info("memLimit : {}", memLimit);
		log.info("gpuLimit : {}", gpuLimit);
		log.info("hour : {}", hour);

		//prometheus에서 기준치 이상을 넘은 워크로드 조회
		List<ResponseDTO.RealTimeDTO> totalList = getOverResourcePodList(cpuLimit, memLimit, gpuLimit, hour);
		List<ResponseDTO.RealTimeDTO> alarmList = getOverResourcePodList(cpuLimit, memLimit, gpuLimit, hour - 1);

		//최적화 대상에 대한 distinct 처리 진행
		List<ResourceOptimizationTargetDTO> alarmDistinctList = alarmList.stream()
			.map(realTimeDTO -> new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName()))
			.distinct()
			.toList();

		List<ModuleWorkloadResDTO> alarmParentList = workloadModuleService.getParentControllerList(
			alarmDistinctList);


		//최적화 대상에 대한 distinct 처리 진행
		List<ResourceOptimizationTargetDTO> list = totalList.stream()
			.map(realTimeDTO -> new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName()))
			.distinct()
			.toList();

		log.info("over resource total pod list count : {}", list.size());
		List<ModuleWorkloadResDTO> parentControllerList = workloadModuleService.getParentControllerList(list);
		int resultCnt = workloadModuleService.optimizationInteractiveWorkload(list);

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
			Promql.RESOURCE_OPTIMIZATION_CPU, String.valueOf(hour), String.valueOf(gpu), unixTimeStamp);
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