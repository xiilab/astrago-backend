package com.xiilab.servermonitor.report.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.ReportDTO;

public interface DbMonitorService {
	ReportDTO.ResourceQuotaDTO getResourceQuota(String startDate, String endDate);

	List<ReportDTO.WorkspaceResourceQuotaDTO> getResourceQuotaByWorkspace(String endDate, String reportType);

}
