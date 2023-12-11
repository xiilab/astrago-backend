package com.xiilab.modulemonitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

@Service
public interface PrometheusService {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(String promql, RequestDTO requestDTO);
}
