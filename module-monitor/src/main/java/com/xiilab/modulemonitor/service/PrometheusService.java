package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

public interface PrometheusService {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO);
	List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO);
	String getRealTimeMetricByQuery(String promql);
	List<ResponseDTO.HistoryDTO> getHistoryMetricByQuery(String promql, String startDate, String endDate);
	List<ResponseDTO.RealTimeDTO> extractMetrics(String jsonResponse, String metricName);
}
