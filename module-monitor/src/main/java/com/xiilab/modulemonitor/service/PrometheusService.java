package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;

public interface PrometheusService {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO);

	List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO);

	String getRealTimeMetricByQuery(String promql);

	List<ResponseDTO.HistoryDTO> getHistoryMetricByQuery(String promql, String startDate, String endDate);

	List<ResponseDTO.RealTimeDTO> extractMetrics(String jsonResponse, String metricName);

	List<ResponseDTO.RealTimeDTO> getRealTimeMetric(Promql promql, String time, String limitResource,
		String unixTimeStamp);

	long getHistoryMetricByReport(String promql, String startDateUnixTime, String endDate, long step);

	List<ResponseDTO.HistoryDTO> getHistoryMetricBySystem(String promql, String startDate, String endDate);

	List<ResponseDTO.HistoryDTO> getHistoryMetricByWarning(String promql, String startDate, String endDate);

	ReportDTO.ResourceDTO getHistoryResourceReport(String promql, String startDate, String endDate, String resourceName);
}
