package com.xiilab.servermonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.ReportDTO;

public interface ReportFacadeService {
	ReportDTO.ResourceUtilDTO getClusterTotalResourceUtil(String endDate, String reportType);

	List<ReportDTO.ResourceDTO> getGpuResourceUtil(String endDate, String reportType);

	List<ReportDTO.ResourceDTO> getCpuResourceUtil(String endDate, String reportType);

	List<ReportDTO.ResourceDTO> getMemResourceUtil(String endDate, String reportType);

	ReportDTO.ResourceDTO getTotalResourceScore(String endDate, String reportType);

	ReportDTO.StatisticsDTO getResourceStatistics(String endDate, String reportType);

	ReportDTO.ResourceQuotaDTO getResourceQuota(String endDate, String reportType);

	List<ReportDTO.WorkspaceResourceQuotaDTO> getResourceQuotaByWorkspace(String endDate, String reportType);

	List<ReportDTO.SystemInfoDTO> getSystemInfo(String endDate, String reportType);

	List<ReportDTO.SystemGpuDTO> getSystemGpuTemp(String endDate, String reportType);

	List<ReportDTO.SystemResDTO> getSystemCpuUsage(String endDate, String reportType);

	List<ReportDTO.SystemResDTO> getSystemMemUsage(String endDate, String reportType);

	List<ReportDTO.SystemResDTO> getSystemDiskUsage(String endDate, String reportType);

}
