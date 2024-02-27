package com.xiilab.modulemonitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Repository
public class PrometheusRepositoryImpl implements PrometheusRepository {
	@Value("${prometheus.url}")
	private String prometheusURL;

	/**
	 * Prometheus 실시간 조회 메소드
	 * @param promql 조회될 Prometheus Query
	 * @return Prometheus에서 조회된 실시간 값
	 */
	public String getRealTimeMetricByQuery(String promql) {
		WebClient webClient = WebClient.builder()
			.baseUrl(prometheusURL)
			.build();
		return webClient
			.get()
			.uri("/api/v1/query?query={promql}", promql)
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}

	/**
	 * Prometheus 과거 데이터 조회 메소드
	 * @param promql 조회될 Prometheus Query
	 * @param startDate 검색 시작 시간
	 * @param endDate 검색 종료 시간
	 * @return Prometheus에서 조회된 과거 값
	 */
	public String getHistoryMetricByQuery(String promql, String startDate, String endDate) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
			prometheusURL + "/api/v1/query_range?query={promql}&start={startDate}&end={endDate}&step=2048", String.class,
			promql, startDate, endDate);
		return responseEntity.getBody();
	}
	public String getHistoryMetricByQuery(String promql, String startDate, String endDate, int step) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
			prometheusURL + "/api/v1/query_range?query={promql}&start={startDate}&end={endDate}&step=" + step, String.class,
			promql, startDate, endDate);
		return responseEntity.getBody();
	}
}
