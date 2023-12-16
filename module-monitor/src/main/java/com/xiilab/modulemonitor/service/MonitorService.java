package com.xiilab.modulemonitor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.repository.CommonRepositoryImpl;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;

import io.fabric8.kubernetes.api.model.Event;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorService {

	private final PrometheusRepository prometheusRepository;
	private final K8sMonitorRepository k8sMonitorRepository;
	private final CommonRepositoryImpl common;

	/**
	 * Prometheus에 query조회하는 메소드
	 * @param requestDTO 조회될 정보가 담긴 객체
	 * @return 조회된 Metrics
	 */
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(RequestDTO requestDTO){

		String promql = getPromql(requestDTO.metricName(), requestDTO);
		if(!promql.isBlank()){
			String metricResult = prometheusRepository.getRealTimeMetricByQuery(promql);
			return extractMetrics(metricResult, requestDTO);
		}else{
			return k8sMonitorRepository.getK8sMetricsByQuery(requestDTO);
		}
	}
	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO){

		// 검색시간 UnixTime로 변환
		String startDate = common.toUnixTime(requestDTO.startDate());
		String endDate = common.toUnixTime(requestDTO.endDate());

		String promql = getPromql(requestDTO.metricName(), requestDTO);
		String result = prometheusRepository.getHistoryMetricByQuery(promql, startDate, endDate);
		return extractHistoryMetrics(result, requestDTO);
	}

	public List<ResponseDTO.EventDTO> getEventList(String namespace, String podName){
		List<Event> eventList = null;
		if(!common.isStringEmpty(namespace) && !common.isStringEmpty(podName)){
			eventList = k8sMonitorRepository.getEventList(namespace, podName);
		}else if(!common.isStringEmpty(namespace)){
			eventList = k8sMonitorRepository.getEventList(namespace);
		}else{
			eventList = k8sMonitorRepository.getEventList();
		}
		return eventToDTO(eventList);
	}


	public List<ResponseDTO.EventDTO> eventToDTO(List<Event> eventList){
		return eventList.stream().map(event ->
			ResponseDTO.EventDTO.builder()
				.type(event.getType())
				.workloadName(event.getMetadata().getNamespace())
				.time(common.formatDateTime(
					event.getEventTime() == null ? event.getLastTimestamp() : event.getEventTime().getTime()
				))
				.reason(event.getReason())
				.message(event.getMessage())
				.build()
		).toList();
	}


	/**
	 * Prometheus Metric List 조회하는 메소드
	 */
	public List<ResponseDTO.PromqlDTO> getPromqlList(){
		return Arrays.stream(Promql.values())
			.map(enumValue -> new ResponseDTO.PromqlDTO(
				enumValue.name(),
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

	/**
	 * 조회된 Prometheus Metrics 추출하여 ResponseDTO List 반환하는 메소드
	 *
	 * @param jsonResponse 조회된 Metric 객체
	 * @param requestDTO Metric 이름
	 * @return 반환될 ResponseDTO List
	 */
	public List<ResponseDTO.RealTimeDTO> extractMetrics(String jsonResponse, RequestDTO requestDTO) {
		List<ResponseDTO.RealTimeDTO> responseDTOS = new ArrayList<>();

		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonResponse);

			// result 필드 추출
			JsonNode results = jsonNode.path("data").path("result");
			for (JsonNode result : results) {
				// 리스트에 추가
				responseDTOS.add(createResponseDTO(result, requestDTO.metricName()));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return responseDTOS;
	}

	/**
	 * JsonNode를 ResponseDTO로 변환하는 메소드
	 *
	 * @param result  Prometheus result 필드 값
	 * @param metric  Metric Name
	 * @return        생성된 ResponseDTO
	 */
	private ResponseDTO.RealTimeDTO createResponseDTO(JsonNode result, String metric) {
		JsonNode metricData = result.path("metric");

		// 결과 값 추출
		double value = result.path("value").get(1).asDouble();

		// ResponseDTO 객체 생성하여 반환
		return new ResponseDTO.RealTimeDTO(
			metric,
			common.formatDateTime(result.path("value").get(0).asDouble()),
			common.getStringOrNull(metricData, "namespace"),
			common.getStringOrNull(metricData, "node"),
			common.getStringOrNull(metricData, "kubernetes_node"),
			common.getStringOrNull(metricData, "pod"),
			common.getStringOrNull(metricData, "instance"),
			common.getStringOrNull(metricData, "modelName"),
			String.valueOf(value)
		);
	}

	/**
	 * 조회된 Prometheus History Metrics 추출하여 HistoryDTO List 반환하는 메소드
	 *
	 * @param jsonResponse 조회된 Metric 객체
	 * @param requestDTO Metric 이름
	 * @return 반환될 HistoryDTO List
	 */
	public List<ResponseDTO.HistoryDTO> extractHistoryMetrics(String jsonResponse, RequestDTO requestDTO){
		List<ResponseDTO.HistoryDTO> responseDTOS = new ArrayList<>();

		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonResponse);

			// result 필드 추출
			JsonNode results = jsonNode.path("data").path("result");
			for (JsonNode result : results) {
				// 리스트에 추가
				responseDTOS.add(createHistoryDTO(result, requestDTO.metricName()));
			}
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return responseDTOS;
	}

	/**
	 * JsonNode를 ResponseDTO로 변환하는 메소드
	 *
	 * @param result  Prometheus result 필드 값
	 * @param metric  Metric Name
	 * @return        생성된 ResponseDTO
	 */
	private ResponseDTO.HistoryDTO createHistoryDTO(JsonNode result, String metric) {
		JsonNode metricData = result.path("metric");

		// 결과 값 추출
		JsonNode values = result.path("values");
		// ResponseDTO 객체 생성하여 반환
		return ResponseDTO.HistoryDTO.builder()
			.nameSpace(common.getStringOrNull(metricData, "namespace"))
			.instance(common.getStringOrNull(metricData, "instance"))
			.metricName(metric)
			.podName(common.getStringOrNull(metricData, "pod"))
			.nodeName(common.getStringOrNull(metricData, "node"))
			.valueDTOS(createHistoryValue(values))
			.build();
	}
	private List<ResponseDTO.ValueDTO>  createHistoryValue(JsonNode values){
		List<ResponseDTO.ValueDTO> valueDTOList = new ArrayList<>();
		if (values.isArray()) {
			for (JsonNode node : values) {
				valueDTOList.add(ResponseDTO.ValueDTO.builder()
					.dateTime(common.formatDateTime(node.get(0).asDouble()))
					.value(node.get(1).textValue())
					.build());
			}
		}
		return valueDTOList;
	}
}
