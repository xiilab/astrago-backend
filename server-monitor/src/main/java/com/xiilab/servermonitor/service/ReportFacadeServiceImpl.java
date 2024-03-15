package com.xiilab.servermonitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportFacadeServiceImpl implements ReportFacadeService{
	private final PrometheusService prometheusService;

	public ReportDTO.ResourceUtilDTO getClusterTotalResourceUtil(String startDate, String reportType){
		String endDateUnixTime = DataConverterUtil.getEndDateUnixTime(startDate, reportType);
		long reportStep = DataConverterUtil.getReportStep(reportType);

		long gpuAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_GPU_UTIL", startDate, endDateUnixTime, reportStep);
		long cpuAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_CPU_UTIL", startDate, endDateUnixTime, reportStep);
		long memAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_MEM_UTIL", startDate, endDateUnixTime, reportStep);

		return ReportDTO.ResourceUtilDTO.builder()
			.gpuUtil(gpuAvg)
			.cpuUtil(cpuAvg)
			.memUtil(memAvg)
			.resourceScore(Math.round((gpuAvg * 0.4) + (cpuAvg * 0.3) + (memAvg * 0.3)))
			.build();
	}

	@Override
	public List<ReportDTO.ResourceDTO> getGpuResourceUtil(String startDate, String reportType) {

		String endDate = DataConverterUtil.getEndDate(startDate, reportType);

		ReportDTO.ResourceDTO gpuTotal = prometheusService.getHistoryResourceReport("REPORT_RESOURCE_TOTAL",
			startDate, endDate, "GPU_TOTAL");
		ReportDTO.ResourceDTO gpuRequest = prometheusService.getHistoryResourceReport(
			"REPORT_RESOURCE_REQUEST",
			startDate, endDate, "GPU_REQUEST");
		ReportDTO.ResourceDTO gpuUsage = prometheusService.getHistoryResourceReport(
			"REPORT_RESOURCE_USAGE",
			startDate, endDate, "GPU_USAGE");

		return List.of(
			gpuTotal,
			gpuRequest,
			gpuUsage);
	}
}
