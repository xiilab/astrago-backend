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
		String promql = getPromql(requestDTO.metricName(), requestDTO);
		return prometheusRepository.getRealTimeMetricByQuery(promql, requestDTO);
	}
	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO){
		String promql = getPromql(requestDTO.metricName(), requestDTO);
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
	private String getPromql(String metricName, RequestDTO requestDTO){
		try{
			return createPromql(Promql.valueOf(metricName), requestDTO);
		}catch (IllegalArgumentException e){
			throw new IllegalArgumentException("해당 이름의 Metric(" + metricName + ")이 없습니다.");
		}
	}

	/**
	 * Promql 생성하는 메소드
	 * @param promql Client가 요청한 promql
	 * @param requestDTO 조건에 추가될 정보가 담긴 객체
	 * @return 생성된 Promql
	 */
	private String createPromql(Promql promql, RequestDTO requestDTO){
		String result = "";
		// GPU일 경우 kubernetes_node 사용
		if (promql.getType().equals("GPU")) {
			if (!requestDTO.nodeName().isBlank()) {
				result = "kubernetes_node=\"" + requestDTO.nodeName() + "\",";
			}
		} else{
			if (!requestDTO.nodeName().isBlank()) {
				result = "namespace=\"" + requestDTO.namespace() + "\",";
			}
		}
		if (!requestDTO.podName().isBlank()) {
			result = result + "pod=\"" + requestDTO.podName() + "\"";
		}
		return String.format(promql.getQuery(), result.toLowerCase());
	}
}
