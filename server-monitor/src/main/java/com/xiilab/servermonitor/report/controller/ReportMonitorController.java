package com.xiilab.servermonitor.report.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.servermonitor.report.service.ReportFacadeService;
import com.xiilab.servermonitor.report.service.ReportMonitorService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/monitor/report")
@RequiredArgsConstructor
public class ReportMonitorController {
	private final ReportFacadeService reportFacadeService;
	private final ReportMonitorService reportMonitorService;
	@GetMapping("/cluster/totalResource")
	@Operation(summary = "Cluster Monitor 전체 리소스 활용률 조회")
	public ResponseEntity<ReportDTO.ResourceUtilDTO> getClusterTotalResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getClusterTotalResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/gpuResourceUtil")
	@Operation(summary = "Cluster Monitor GPU 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getGpuResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getGpuResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/cpuResourceUtil")
	@Operation(summary = "Cluster Monitor CPU 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getCpuResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getCpuResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/memResourceUtil")
	@Operation(summary = "Cluster Monitor MEM 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getMemResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getMemResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/totalScore")
	@Operation(summary = "Cluster Monitor 리소스 활용 점수 추이 조회")
	public ResponseEntity<ReportDTO.ResourceDTO> getTotalResourceScore(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getTotalResourceScore(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceScoreByWorkspace")
	@Operation(summary = "워크스페이스별 리소스 활용 통계 조회")
	public ResponseEntity<ReportDTO.StatisticsDTO> getResourceStatistics(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceStatistics(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceQuota")
	@Operation(summary = "리소스 신청/승인/반려 통계")
	public ResponseEntity<ReportDTO.ResourceQuotaDTO> getResourceQuota(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceQuota(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceQuotaByWorkspace")
	@Operation(summary = "리소스 신청/승인/반려 통계")
	public ResponseEntity<List<ReportDTO.WorkspaceResourceQuotaDTO>> getResourceQuotaByWorkspace(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceQuotaByWorkspace(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/info")
	@Operation(summary = "서버 시스템 정보 조회")
	public ResponseEntity<List<ReportDTO.SystemInfoDTO>> getSystemInfo(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getSystemInfo(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/temp")
	@Operation(summary = "시스템 이상경고 - GPU 온도 90도 이상")
	public ResponseEntity<List<ReportDTO.SystemGpuDTO>> getSystemGpuTemp(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemGpuTemp(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/cpu")
	@Operation(summary = "시스템 이상 경고 - CPU 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemCpuUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemCpuUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/mem")
	@Operation(summary = "시스템 이상 경고 - MEM 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemMemUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemMemUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/disk")
	@Operation(summary = "시스템 이상 경고 - DISK 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemDiskUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemDiskUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/nodeSystemUsage")
	@Operation(summary = "노드별 시스템 사용 현황")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getNodeSystemUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getNodeSystemUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/nodeGpuUsage")
	@Operation(summary = "노드별 GPU 사용 현황")
	public ResponseEntity<List<ReportDTO.SystemGpuDTO>> getNodeGpuUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getNodeGpuUsage(endDate, reportType), HttpStatus.OK);
	}

	@PatchMapping("/{id}/{enable}")
	@Operation(summary = "Report 예약 발송 on/off")
	public ResponseEntity<HttpStatus> reportOnOff(@PathVariable(name = "id") Long id,
		@PathVariable(name = "enable") boolean enable){

		reportMonitorService.reportOnOff(id, enable);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private static List<ReportDTO.SystemValueDTO> getAvgValueDtos(ResponseDTO.HistoryDTO res) {
		return res.valueDTOS().stream().map(valueDTO ->
			ReportDTO.SystemValueDTO.builder()
				.date(DataConverterUtil.dateFormatMMDD(valueDTO.dateTime()))
				.value(DataConverterUtil.formatRoundTo(valueDTO.value()))
				.build()).toList();
	}

	private static List<ReportDTO.SystemValueDTO> getAvgValueDtosOverEight(ResponseDTO.HistoryDTO res) {
		return List.of(
			ReportDTO.SystemValueDTO.builder()
				.date(DataConverterUtil.dateFormatMMDD(res.valueDTOS().get(0).dateTime()))
				.value(res.valueDTOS().stream()
					.mapToDouble(valueDTO -> DataConverterUtil.formatRoundTo(valueDTO.value()))
					.average().orElse(0.00)).build()
		);
	}

	/*
		@Override
	public List<ReportDTO.SystemGpuDTO> getSystemGpuTemp(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);
		List<ReportDTO.SystemGpuDTO> result = new ArrayList<>();

		List<ResponseDTO.HistoryDTO> avgTemp = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_AVG_GPU_TEMP", startDate, endDate);

		List<ResponseDTO.HistoryDTO> maxTemp = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_MAX_GPU_TEMP", startDate, endDate);

		avgTemp.addAll(maxTemp);

		Map<String, Map<String, List<ResponseDTO.HistoryDTO>>> gpuInfo = avgTemp.stream()
			.collect(Collectors.groupingBy(ResponseDTO.HistoryDTO::kubeNodeName, Collectors.groupingBy(
				ResponseDTO.HistoryDTO::gpuIndex)));

		for (Map.Entry<String, Map<String, List<ResponseDTO.HistoryDTO>>> entry : gpuInfo.entrySet()) {
			for (Map.Entry<String, List<ResponseDTO.HistoryDTO>> ent : entry.getValue().entrySet()) {
				ReportDTO.SystemGpuDTO systemGpuDTO = ReportDTO.SystemGpuDTO.builder().build();
				ReportDTO.SystemCategoryDTO avgCategory = ReportDTO.SystemCategoryDTO.builder().build();
				ReportDTO.SystemCategoryDTO maxCategory = null; // 기존에는 여기서 초기화되었으나, 조건을 만족할 때만 초기화합니다.
				for (ResponseDTO.HistoryDTO res : ent.getValue()) {
					switch (res.metricName()) {
						case "REPORT_SYSTEM_AVG_GPU_TEMP" -> {
							systemGpuDTO.setServerName(entry.getKey());
							systemGpuDTO.setGpuIndex(Long.parseLong(res.gpuIndex()));
							avgCategory.setCategory("GPU 온도 평균");
							if (res.valueDTOS().size() > 8) {
								avgCategory.setValueDTOS(
									List.of(
										ReportDTO.SystemValueDTO.builder()
											.date(DataConverterUtil.dateFormatMMDD(res.valueDTOS().get(0).dateTime()))
											.value(res.valueDTOS().stream()
												.mapToDouble(
													valueDTO -> DataConverterUtil.formatRoundTo(valueDTO.value()))
												.average().orElse(0.00)).build()
									)
								);
							} else {
								avgCategory.setValueDTOS(res.valueDTOS().stream().map(valueDTO ->
									ReportDTO.SystemValueDTO.builder()
										.date(DataConverterUtil.dateFormatMMDD(valueDTO.dateTime()))
										.value(DataConverterUtil.formatRoundTo(valueDTO.value()))
										.build()).toList()
								);
							}
						}
						case "REPORT_SYSTEM_MAX_GPU_TEMP" -> {
							if (maxCategory == null) {
								maxCategory = ReportDTO.SystemCategoryDTO.builder().build();
								maxCategory.setCategory("GPU 온도 최대");

								List<ResponseDTO.ValueDTO> list = res.valueDTOS()
									.stream()
									.filter(valueDTO -> (DataConverterUtil.formatRoundTo(valueDTO.value()) > 90))
									.toList();

								if (!Objects.requireNonNull(list).isEmpty()) {
									if (res.valueDTOS().size() > 8) {
										maxCategory.setValueDTOS(
											List.of(
												ReportDTO.SystemValueDTO.builder()
													.date(DataConverterUtil.dateFormatMMDD(res.valueDTOS().get(0).dateTime()))
													.value(res.valueDTOS()
														.stream()
														.mapToDouble(value ->
															DataConverterUtil.formatRoundTo(value.value()))
														.average()
														.orElse(0.00))
													.build()
											)
										);
									} else {
										maxCategory.setValueDTOS(
											res.valueDTOS().stream()
												.map(valueDTO -> ReportDTO.SystemValueDTO.builder()
													.date(DataConverterUtil.dateFormatMMDD(valueDTO.dateTime()))
													.value(DataConverterUtil.formatRoundTo(valueDTO.value()))
													.build()).toList()
										);
									}
								}
							}
						}
					}
				}
				if (Objects.nonNull(maxCategory.getValueDTOS())) {
					systemGpuDTO.setCategoryDTOS(List.of(avgCategory, maxCategory));
					result.add(systemGpuDTO);
				}
			}
		}

		return result;
	}
	*/
}
