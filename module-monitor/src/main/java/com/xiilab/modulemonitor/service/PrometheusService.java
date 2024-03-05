package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
public interface PrometheusService {
	List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO);
	List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO);
	String getRealTimeMetricByQuery(String promql);

	String getHistoryMetricByQuery(String promql, String startDate, String endDate);

	List<ResponseDTO.RealTimeDTO> getRealTimeMetric(Promql promql, String time, String limitResource, String unixTimeStamp);
}
