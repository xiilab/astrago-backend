package com.xiilab.modulemonitor.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.modulemonitor.service.PrometheusRepository;

@Repository
public class PrometheusRepositoryImpl implements PrometheusRepository {
	@Value("${prometheus.url}")
	private String prometheusURL;

	public String getRealTimeMetricByQuery(String query){
		WebClient webClient = WebClient.builder()
			.baseUrl(prometheusURL)
			.build();
		return webClient
			.get()
			.uri("/api/v1/query?query={query}", query)
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}


	public String getHistoryMetricByQuery(String promql, String startDate, String endDate){
		RestTemplate restTemplate =    new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
			prometheusURL + "/api/v1/query_range?query={promql}&start={startDate}&end={endDate}&step=256", String.class, promql, startDate, endDate);
		return responseEntity.getBody();
	}
}
