package com.xiilab.servermonitor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportFacadeServiceImpl implements ReportFacadeService {
	private final PrometheusService prometheusService;
	private final DbMonitorService dbMonitorService;

	public ReportDTO.ResourceUtilDTO getClusterTotalResourceUtil(String endDate, String reportType) {
		String startDateUnixTime = DataConverterUtil.getEndDateUnixTime(endDate, reportType);
		long reportStep = DataConverterUtil.getReportStep(reportType);

		long gpuAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_GPU_UTIL", startDateUnixTime, endDate, reportStep);
		long cpuAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_CPU_UTIL", startDateUnixTime, endDate, reportStep);
		long memAvg = prometheusService.getHistoryMetricByReport(
			"REPORT_CLUSTER_MEM_UTIL", startDateUnixTime, endDate, reportStep);

		return ReportDTO.ResourceUtilDTO.builder()
			.gpuUtil(gpuAvg)
			.cpuUtil(cpuAvg)
			.memUtil(memAvg)
			.resourceScore(Math.round((gpuAvg * 0.4) + (cpuAvg * 0.3) + (memAvg * 0.3)))
			.build();
	}

	@Override
	public List<ReportDTO.ResourceDTO> getGpuResourceUtil(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		ReportDTO.ResourceDTO gpuTotal = prometheusService.getHistoryResourceReport("REPORT_GPU_RESOURCE_TOTAL",
			endDate, startDate, "GPU_TOTAL");
		ReportDTO.ResourceDTO gpuRequest = prometheusService.getHistoryResourceReport(
			"REPORT_GPU_RESOURCE_REQUEST",
			endDate, startDate, "GPU_REQUEST");
		ReportDTO.ResourceDTO gpuUsage = prometheusService.getHistoryResourceReport(
			"REPORT_GPU_RESOURCE_USAGE",
			endDate, startDate, "GPU_USAGE");

		return List.of(
			gpuTotal,
			gpuRequest,
			gpuUsage);
	}

	@Override
	public List<ReportDTO.ResourceDTO> getCpuResourceUtil(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		ReportDTO.ResourceDTO cpuTotal = prometheusService.getHistoryResourceReport("REPORT_CPU_RESOURCE_TOTAL",
			endDate, startDate, "CPU_TOTAL");
		ReportDTO.ResourceDTO cpuRequest = prometheusService.getHistoryResourceReport(
			"REPORT_CPU_RESOURCE_REQUEST",
			endDate, startDate, "CPU_REQUEST");
		ReportDTO.ResourceDTO cpuUsage = prometheusService.getHistoryResourceReport(
			"REPORT_CPU_RESOURCE_USAGE",
			endDate, startDate, "CPU_USAGE");

		return List.of(
			cpuTotal,
			cpuRequest,
			cpuUsage);
	}

	@Override
	public List<ReportDTO.ResourceDTO> getMemResourceUtil(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		ReportDTO.ResourceDTO memTotal = prometheusService.getHistoryResourceReport("REPORT_MEM_RESOURCE_TOTAL",
			endDate, startDate, "MEM_TOTAL");
		ReportDTO.ResourceDTO memRequest = prometheusService.getHistoryResourceReport(
			"REPORT_MEM_RESOURCE_REQUEST",
			endDate, startDate, "MEM_REQUEST");
		ReportDTO.ResourceDTO memUsage = prometheusService.getHistoryResourceReport(
			"REPORT_MEM_RESOURCE_USAGE",
			endDate, startDate, "MEM_USAGE");

		return List.of(
			memTotal,
			memRequest,
			memUsage);
	}

	@Override
	public ReportDTO.ResourceDTO getTotalResourceScore(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		ReportDTO.ResourceDTO memTotal = prometheusService.getHistoryResourceReport("REPORT_TOTAL_SCORE",
			endDate, startDate, "TOTAL_RESOURCE_SCORE");

		return memTotal;
	}

	@Override
	public ReportDTO.StatisticsDTO getResourceStatistics(String endDate, String reportType) {
		/**
		 * 어떻게 해야될지 모르겠음....
		 */
		return null;
	}

	@Override
	public ReportDTO.ResourceQuotaDTO getResourceQuota(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		return dbMonitorService.getResourceQuota(startDate, endDate);
	}

	@Override
	public List<ReportDTO.WorkspaceResourceQuotaDTO> getResourceQuotaByWorkspace(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		return dbMonitorService.getResourceQuotaByWorkspace(endDate, startDate);
	}

	@Override
	public List<ReportDTO.SystemInfoDTO> getSystemInfo(String endDate, String reportType) {

		List<ReportDTO.SystemInfoDTO> result = new ArrayList<>();
		String startDate = DataConverterUtil.getEndDate(endDate, reportType);

		List<ResponseDTO.HistoryDTO> cpuInfo = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_CPU", startDate, endDate);

		List<ResponseDTO.HistoryDTO> memInfo = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_MEM", startDate, endDate);

		List<ResponseDTO.HistoryDTO> diskInfo = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_DISK", startDate, endDate);

		List<ResponseDTO.HistoryDTO> gpuInfo = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_GPU", startDate, endDate);

		List<ResponseDTO.HistoryDTO> osInfo = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_OS", startDate, endDate);

		List<ResponseDTO.HistoryDTO> gpuModel = prometheusService.getHistoryMetricBySystem(
			"REPORT_SYSTEM_INFO_GPU_MODEL", startDate, endDate);

		cpuInfo.addAll(memInfo);
		cpuInfo.addAll(diskInfo);
		cpuInfo.addAll(gpuInfo);
		cpuInfo.addAll(osInfo);
		cpuInfo.addAll(gpuModel);

		Map<String, List<ResponseDTO.HistoryDTO>> systemInfo = cpuInfo.stream()
			.collect(Collectors.groupingBy(ResponseDTO.HistoryDTO::nodeName));

		for (Map.Entry<String, List<ResponseDTO.HistoryDTO>> entry : systemInfo.entrySet()) {

			ReportDTO.SystemInfoDTO systemInfoDTO = ReportDTO.SystemInfoDTO.builder().build();

			for (ResponseDTO.HistoryDTO value : entry.getValue()) {

				switch (value.metricName()) {
					case "REPORT_SYSTEM_INFO_CPU" -> {
						systemInfoDTO.setServerName(value.nodeName());
						systemInfoDTO.setIp(value.instance());
						systemInfoDTO.setCpu(Long.parseLong(value.valueDTOS().get(0).value()));
					}
					case "REPORT_SYSTEM_INFO_MEM" ->
						systemInfoDTO.setMem(Long.parseLong(value.valueDTOS().get(0).value()));
					case "REPORT_SYSTEM_INFO_DISK" ->
						systemInfoDTO.setDisk(Long.parseLong(value.valueDTOS().get(0).value()));
					case "REPORT_SYSTEM_INFO_GPU" ->
						systemInfoDTO.setGpu(Long.parseLong(value.valueDTOS().get(0).value()));
					case "REPORT_SYSTEM_INFO_OS" -> systemInfoDTO.setOs(value.prettyName());
					case "REPORT_SYSTEM_INFO_GPU_MODEL" -> systemInfoDTO.setGpuModelName(value.modelName());
				}
			}
			result.add(systemInfoDTO);
		}

		return result;
	}

	@Override
	public List<ReportDTO.SystemGpuDTO> getSystemGpuTemp(String endDate, String reportType) {

		String startDate = DataConverterUtil.getEndDate(endDate, reportType);
		List<ReportDTO.SystemGpuDTO> result = new ArrayList<>();

		List<ResponseDTO.HistoryDTO> avgTemp = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_AVG_TEMP", startDate, endDate);

		List<ResponseDTO.HistoryDTO> maxTemp = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_MAX_TEMP", startDate, endDate);

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
						case "REPORT_SYSTEM_AVG_TEMP" -> {
							systemGpuDTO.setServerName(entry.getKey());
							systemGpuDTO.setGpuIndex(Long.parseLong(res.gpuIndex()));
							avgCategory.setCategory("GPU 온도 평균");
							if (res.valueDTOS().size() > 8) {
								avgCategory.setValueDTOS(
									List.of(
										ReportDTO.SystemValueDTO.builder()
											.date("avg")
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
						case "REPORT_SYSTEM_MAX_TEMP" -> {
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
													.date("avg")
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

	@Override
	public List<ReportDTO.SystemResDTO> getSystemCpuUsage(String endDate, String reportType) {
		String startDate = DataConverterUtil.getEndDate(endDate, reportType);
		List<ReportDTO.SystemResDTO> result = new ArrayList<>();

		List<ResponseDTO.HistoryDTO> avgCpuUsage = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_AVG_CPU", startDate, endDate);

		List<ResponseDTO.HistoryDTO> maxCpuUsage = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_MAX_CPU", startDate, endDate);

		avgCpuUsage.addAll(maxCpuUsage);

		Map<String, List<ResponseDTO.HistoryDTO>> cpuUsage = avgCpuUsage.stream()
			.collect(Collectors.groupingBy(ResponseDTO.HistoryDTO::nodeName));

		return extracted(cpuUsage, result);
	}

	private List<ReportDTO.SystemResDTO> extracted(Map<String, List<ResponseDTO.HistoryDTO>> cpuUsage,
		List<ReportDTO.SystemResDTO> result) {

		for (Map.Entry<String, List<ResponseDTO.HistoryDTO>> entry : cpuUsage.entrySet()) {
			ReportDTO.SystemResDTO systemResDTO = ReportDTO.SystemResDTO.builder().build();
			ReportDTO.SystemCategoryDTO avgCategory = ReportDTO.SystemCategoryDTO.builder().build();
			ReportDTO.SystemCategoryDTO maxCategory = null; // 기존에는 여기서 초기화되었으나, 조건을 만족할 때만 초기화합니다.

			for (ResponseDTO.HistoryDTO res : entry.getValue()) {
				switch (res.metricName()) {
					case "REPORT_SYSTEM_AVG_CPU" -> {
						systemResDTO.setServerName(entry.getKey());
						avgCategory.setCategory("CPU(%) 평균");
						if (res.valueDTOS().size() > 8) {
							avgCategory.setValueDTOS(
								List.of(
									ReportDTO.SystemValueDTO.builder()
										.date("avg")
										.value(res.valueDTOS().stream()
											.mapToDouble(valueDTO -> DataConverterUtil.formatRoundTo(valueDTO.value()))
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
					case "REPORT_SYSTEM_MAX_CPU" -> {
						if (maxCategory == null) {
							maxCategory = ReportDTO.SystemCategoryDTO.builder().build();
							maxCategory.setCategory("CPU(%) 최대");

							List<ResponseDTO.ValueDTO> list = res.valueDTOS()
								.stream()
								.filter(valueDTO -> (DataConverterUtil.formatRoundTo(valueDTO.value()) > 90))
								.toList();

							if (!Objects.requireNonNull(list).isEmpty()) {
								if (res.valueDTOS().size() > 8) {
									maxCategory.setValueDTOS(
										List.of(
											ReportDTO.SystemValueDTO.builder()
												.date("avg")
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
					case "REPORT_SYSTEM_AVG_MEM" -> {
						systemResDTO.setServerName(entry.getKey());
						avgCategory.setCategory("MEM(%) 평균");
						if (res.valueDTOS().size() > 8) {
							avgCategory.setValueDTOS(
								List.of(
									ReportDTO.SystemValueDTO.builder()
										.date("avg")
										.value(res.valueDTOS().stream()
											.mapToDouble(valueDTO -> DataConverterUtil.formatRoundTo(valueDTO.value()))
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
					case "REPORT_SYSTEM_MAX_MEM" -> {
						if (maxCategory == null) {
							maxCategory = ReportDTO.SystemCategoryDTO.builder().build();
							maxCategory.setCategory("MEM(%) 최대");

							List<ResponseDTO.ValueDTO> list = res.valueDTOS()
								.stream()
								.filter(valueDTO -> (DataConverterUtil.formatRoundTo(valueDTO.value()) > 90))
								.toList();

							if (!Objects.requireNonNull(list).isEmpty()) {
								if (res.valueDTOS().size() > 8) {
									maxCategory.setValueDTOS(
										List.of(
											ReportDTO.SystemValueDTO.builder()
												.date("avg")
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
				systemResDTO.setCategoryDTOS(List.of(avgCategory, maxCategory));
				result.add(systemResDTO);
			}
		}
		return result;
	}

	@Override
	public List<ReportDTO.SystemResDTO> getSystemMemUsage(String endDate, String reportType) {
		String startDate = DataConverterUtil.getEndDate(endDate, reportType);
		List<ReportDTO.SystemResDTO> result = new ArrayList<>();

		List<ResponseDTO.HistoryDTO> avgMemUsage = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_AVG_MEM", startDate, endDate);

		List<ResponseDTO.HistoryDTO> maxMemUsage = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_MAX_MEM", startDate, endDate);

		avgMemUsage.addAll(maxMemUsage);

		Map<String, List<ResponseDTO.HistoryDTO>> cpuUsage = avgMemUsage.stream()
			.collect(Collectors.groupingBy(ResponseDTO.HistoryDTO::nodeName));

		return extracted(cpuUsage, result);
	}

	@Override
	public List<ReportDTO.SystemResDTO> getSystemDiskUsage(String endDate, String reportType) {
		String startDate = DataConverterUtil.getEndDate(endDate, reportType);
		List<ResponseDTO.HistoryDTO> avgMemUsageList = prometheusService.getHistoryMetricByWarning(
			"REPORT_SYSTEM_DISK_USAGE", startDate, endDate);

		return avgMemUsageList.stream().map(historyDTO -> ReportDTO.SystemResDTO.builder()
			.serverName(historyDTO.nodeName())
			.categoryDTOS(List.of(
				ReportDTO.SystemCategoryDTO.builder()
					.category("디스크(%) 사용량")
					.valueDTOS(historyDTO.valueDTOS().stream().map(valueDTO -> ReportDTO.SystemValueDTO.builder()
						.date(DataConverterUtil.dateFormatMMDD(valueDTO.dateTime()))
						.value(DataConverterUtil.formatRoundTo(valueDTO.value()))
						.build()
					).toList())
					.build()
			))
			.build()
		).toList();
	}

}
