package com.xiilab.modulemonitor.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

@Service
public interface PrometheusRepository {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(String promql, RequestDTO requestDTO);
	List<ResponseDTO.HistoryDTO> getHistoryMetricByQuery(String promql, RequestDTO requestDTO);
}
