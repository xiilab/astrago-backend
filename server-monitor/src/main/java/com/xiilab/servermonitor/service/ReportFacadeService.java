package com.xiilab.servermonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.ReportDTO;

public interface ReportFacadeService {
	ReportDTO.ResourceUtilDTO getClusterTotalResourceUtil(String startDate, String reportType);

	List<ReportDTO.ResourceDTO> getGpuResourceUtil(String startDate, String reportType);
}
