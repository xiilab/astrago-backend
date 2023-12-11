package com.xiilab.modulemonitor.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

@Repository
public class PrometheusServiceImpl implements PrometheusService{
	@Value("${prometheus.url}")
	private String prometheusURL;

	public List<ResponseDTO.RealTimeDTO> getRealTimeMetricByQuery(String query, RequestDTO requestDTO){
		WebClient webClient = WebClient.builder()
			.baseUrl(prometheusURL)
			.build();
		String result = webClient
			.get()
			.uri("/api/v1/query?query={query}", query)
			.retrieve()
			.bodyToMono(String.class)
			.block();
		return extractMetrics(result, requestDTO);
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
			formatDateTime(result),
			getStringOrNull(metricData, "instance"),
			getStringOrNull(metricData, "namespace"),
			getStringOrNull(metricData, "node"),
			getStringOrNull(metricData, "pod"),
			String.valueOf(value)
		);
	}
	/**
	 * DateTime 포멧하는 메소드
	 * @param result  Prometheus result 필드 값
	 * @return 포멧된 DateTime
	 */
	private String formatDateTime(JsonNode result) {
		double unixTime = result.path("value").get(0).asDouble();
		// UnixTime LocalDateTime으로 변환
		LocalDateTime dateTime = Instant.ofEpochSecond((long) unixTime)
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTime.format(formatter);
	}
	/**
	 * JsonNode로부터 필드 값을 가져오거나 Null을 반환하는 메서드입니다.
	 *
	 * @param node       JsonNode
	 * @param fieldName  필드 이름
	 * @return           가져온 필드 값 또는 Null
	 */
	private String getStringOrNull(JsonNode node, String fieldName) {
		JsonNode field = node.get(fieldName);
		return field == null ? "" : field.asText();
	}
}
