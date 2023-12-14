package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

public interface PrometheusRepository {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(String promql, RequestDTO requestDTO);
	List<ResponseDTO.HistoryDTO> getHistoryMetricByQuery(String promql, RequestDTO requestDTO);
}
