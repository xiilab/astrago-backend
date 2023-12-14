package com.xiilab.modulemonitor.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorService {

	private final PrometheusRepository prometheusRepository;

	/**
	 * Prometheus에 query조회하는 메소드
	 * @param requestDTO 조회될 정보가 담긴 객체
	 * @return 조회된 Metrics
	 */
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(RequestDTO requestDTO){
		String promql = getPromql(requestDTO.metricName());
		return prometheusRepository.getRealTimeMetricByQuery(promql, requestDTO);
	}
	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO){
		String promql = getPromql(requestDTO.metricName());
		return prometheusRepository.getHistoryMetricByQuery(promql, requestDTO);
	}
	/**
	 * Prometheus Metric List 조회하는 메소드
	 */
	public List<ResponseDTO.PromqlDTO> getPromqlList(){
		return Arrays.stream(Promql.values())
			.map(enumValue -> new ResponseDTO.PromqlDTO(
				enumValue.getQuery(),
				enumValue.getDescription(),
				enumValue.getType()))
			.toList();
	}
	/**
	 * Promql 조회하는 메소드
	 * @param metricName 조회될 metric Name
	 * @return 조회된 Promql
	 */
	public String getPromql(String metricName){
		try{
			return Promql.valueOf(metricName).getQuery();
		}catch (IllegalArgumentException e){
			throw new IllegalArgumentException("해당 이름의 Metric(" + metricName + ")이 없습니다.");
		}
	}

}
