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

import com.xiilab.modulealert.dto.SystemAlertDTO;
import com.xiilab.modulealert.enumeration.SystemAlertMessage;
import com.xiilab.modulealert.enumeration.SystemAlertType;
import com.xiilab.modulealert.service.SystemAlertService;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class BatchOptimizationAlertJob extends QuartzJobBean {
	private ApplicationContext applicationContext;
	private WorkloadModuleService workloadModuleService;
	private PrometheusService prometheusService;
	private SystemAlertService alertService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			//quartz는 Spring bean 사용이 안되기에 수동으로 의존성을 주입한다.
			applicationContext = (ApplicationContext)context.getScheduler().getContext().get("applicationContext");
			workloadModuleService = applicationContext.getBean(WorkloadModuleService.class);
			prometheusService = applicationContext.getBean(PrometheusService.class);
			alertService = applicationContext.getBean(SystemAlertService.class);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}

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

		//astra에서 생성한 실행중인 interactive workload 조회
		List<ModuleWorkloadResDTO> astraWorkloadList = workloadModuleService.getAstraInteractiveWorkloadList();
		log.info("astra에서 생성된 workload 총 개수 : {}", astraWorkloadList.size());

		//통합을 위한 리스트 생성
		List<ResponseDTO.RealTimeDTO> totalList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now().minusHours(hour);
		String unixTimeStamp = String.valueOf(now.atZone(ZoneId.systemDefault()).toEpochSecond());

		//prometheus에서 기준치 이상을 넘은 워크로드 조회
		List<ResponseDTO.RealTimeDTO> overResourceCPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_CPU, String.valueOf(hour), String.valueOf(cpuLimit), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> overResourceGPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_CPU, String.valueOf(hour), String.valueOf(gpuLimit), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> overResourceMEMPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_MEM, String.valueOf(hour), String.valueOf(memLimit), unixTimeStamp);
		log.info("over resource cpu pod list count : {}", overResourceCPUPodList.size());
		log.info("over resource gpu pod list count : {}", overResourceGPUPodList.size());
		log.info("over resource mem pod list count : {}", overResourceMEMPodList.size());

		totalList.addAll(overResourceCPUPodList);
		totalList.addAll(overResourceGPUPodList);
		totalList.addAll(overResourceMEMPodList);

		//최적화 대상에 대한 distinct 처리 진행
		List<ResourceOptimizationTargetDTO> list = totalList.stream()
			.map(realTimeDTO -> new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName()))
			.distinct()
			.toList();

		log.info("over resource total pod list count : {}", list.size());

		List<ModuleWorkloadResDTO> parentControllerList = workloadModuleService.getParentControllerList(list);

		//삭제 될 리소스에 대한 알림
		for (ModuleWorkloadResDTO moduleWorkloadResDTO : parentControllerList) {
			alertService.sendAlert(SystemAlertDTO.builder()
				.recipientId(moduleWorkloadResDTO.getCreatorId())
				.systemAlertType(SystemAlertType.WORKLOAD)
				.message(String.format(SystemAlertMessage.RESOURCE_OPTIMIZATION_ALERT.getMessage(),
					moduleWorkloadResDTO.getWorkspaceName(), moduleWorkloadResDTO.getName()))
				.senderId("SYSTEM")
				.build());
		}
	}
}
