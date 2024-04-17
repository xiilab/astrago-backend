package com.xiilab.modulemonitor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.repository.PrometheusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrometheusServiceImpl implements PrometheusService{
	private final PrometheusRepository prometheusRepository;

	@Override
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO) {
		// Promql 생성
		String promql = getPromql(requestDTO);
		// Prometheus 조회
		String realTimeMetricByQuery = prometheusRepository.getRealTimeMetricByQuery(promql);
		return extractMetrics(realTimeMetricByQuery, requestDTO.metricName());
	}

	@Override
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO) {
		long step = DataConverterUtil.getStep(requestDTO.startDate(), requestDTO.endDate());
		// 검색시간 UnixTime로 변환
		String startDate = DataConverterUtil.toUnixTime(requestDTO.startDate());
		String endDate = DataConverterUtil.toUnixTime(requestDTO.endDate());
		// Promql 생성
		String promql = getPromql(requestDTO);
		String result = prometheusRepository.getHistoryMetricByQuery(promql, startDate, endDate, step);
		return extractHistoryMetrics(result, requestDTO.metricName());
	}

	@Override
	public String getRealTimeMetricByQuery(String promql) {
		return prometheusRepository.getRealTimeMetricByQuery(promql);
	}

	@Override
	public List<ResponseDTO.HistoryDTO> getHistoryMetricByQuery(String promql, String startDate, String endDate) {
		String historyMetricByQuery = prometheusRepository.getHistoryMetricByQuery(promql, startDate, endDate);

		return extractHistoryMetrics(historyMetricByQuery, "");
	}

	@Override
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetric(Promql promql, String time, String limitResource, String unixTimeStamp) {
		String realTimeMetricByQuery = prometheusRepository.getRealTimeMetricByQuery(
			String.format(promql.getQuery(), time, limitResource, unixTimeStamp));
		return extractMetrics(realTimeMetricByQuery, promql.name());
	}

	@Override
	public long getHistoryMetricByReport(String promql, String startDateUnixTime, String endDate, long step) {

		// 검색시간 UnixTime로 변환
		String endDateUnixTime = DataConverterUtil.toUnixTime(endDate);

		String historyMetric = prometheusRepository.getHistoryMetricByQuery(Promql.valueOf(promql).getQuery(),
			startDateUnixTime, endDateUnixTime, 4000L);
		return getAvgHistoryMetric(historyMetric);
	}

	@Override
	public List<ResponseDTO.HistoryDTO> getHistoryMetricBySystem(String promql, String startDate, String endDate) {

		long systemStep = DataConverterUtil.getSystemStep(startDate, endDate);
		// 2024.02.03(start) ~ 2024.02.15(end)
		String startUnixTime = DataConverterUtil.toUnixTime(startDate);
		String endUnixTime = DataConverterUtil.toUnixTime(endDate);

		String historyMetric = prometheusRepository.getHistoryMetricByQuery(Promql.valueOf(promql).getQuery(), startUnixTime, endUnixTime, systemStep);

		return extractHistoryMetrics(historyMetric, promql);
	}

	@Override
	public List<ResponseDTO.HistoryDTO> getHistoryMetricByWarning(String promql, String startDate, String endDate) {
		String startUnixTime = DataConverterUtil.toUnixTime(startDate);
		String endUnixTime = DataConverterUtil.toUnixTime(endDate);

		String historyMetric = prometheusRepository.getHistoryMetricByQuery(Promql.valueOf(promql).getQuery(), startUnixTime, endUnixTime, 86400L);

		return extractHistoryMetrics(historyMetric, promql);
	}

	@Override
	public List<ResponseDTO.HistoryDTO> getHistoryMetricByWarning(String promql, String startDate, String endDate, Long step) {
		String startUnixTime = DataConverterUtil.toUnixTime(startDate);
		String endUnixTime = DataConverterUtil.toUnixTime(endDate);

		String historyMetric = prometheusRepository.getHistoryMetricByQuery(Promql.valueOf(promql).getQuery(), startUnixTime, endUnixTime, step);

		return extractHistoryMetrics(historyMetric, promql);
	}

	@Override
	public ReportDTO.ResourceDTO getHistoryResourceReport(String promql, String startDate, String endDate, String resourceName) {
		long step = DataConverterUtil.getStep(endDate, startDate);
		// 검색시간 UnixTime로 변환
		String startDateUnix = DataConverterUtil.toUnixTime(startDate);
		String endDateUnix = DataConverterUtil.toUnixTime(endDate);

		String historyMetricByQuery = prometheusRepository.getHistoryMetricByQuery(Promql.valueOf(promql).getQuery(),
			endDateUnix, startDateUnix, step);


		return extractResourceMetrics(historyMetricByQuery, resourceName);
	}

	private ReportDTO.ResourceDTO extractResourceMetrics(String metric, String resourceName){
		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			JsonNode jsonparser = DataConverterUtil.jsonparser(metric);
			// result 필드 추출
			JsonNode result = jsonparser.path("data").path("result");

			return createResourceDTO(result, resourceName);

		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private long getAvgHistoryMetric(String historyMetric){
		long total = 0;
		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(historyMetric);
			// result 필드 추출
			JsonNode results = jsonNode.path("data").path("result");

			JsonNode values;

			if(results.get(0) != null) {
				values = results.get(0).get("values");
				for (JsonNode value : values) {
					// 리스트에 추가
					total = total + Long.parseLong(value.get(1).textValue());
				}
				return total / values.size();
			}else {
				return 0;
			}
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * 조회된 Prometheus Metrics 추출하여 ResponseDTO List 반환하는 메소드
	 *
	 * @param jsonResponse 조회된 Metric 객체
	 * @param metricName Metric 이름
	 * @return 반환될 ResponseDTO List
	 */
	@Override
	public List<ResponseDTO.RealTimeDTO> extractMetrics(String jsonResponse, String metricName) {
		List<ResponseDTO.RealTimeDTO> responseDTOS = new ArrayList<>();

		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			JsonNode jsonparser = DataConverterUtil.jsonparser(jsonResponse);
			// result 필드 추출
			JsonNode results = jsonparser.path("data").path("result");
			for (JsonNode result : results) {
				// 리스트에 추가
				responseDTOS.add(createResponseDTO(result, metricName));
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
	 * @return 생성된 ResponseDTO
	 */
	private ResponseDTO.RealTimeDTO createResponseDTO(JsonNode result, String metric) {
		JsonNode metricData = result.path("metric");

		// 결과 값 추출
		double value = result.path("value").get(1).asDouble();

		// ResponseDTO 객체 생성하여 반환
		return new ResponseDTO.RealTimeDTO(
			metric,
			DataConverterUtil.formatDateTime(result.path("value").get(0).asDouble()),
			DataConverterUtil.getStringOrNull(metricData, "namespace"),
			DataConverterUtil.getStringOrNull(metricData, "node"),
			DataConverterUtil.getStringOrNull(metricData, "kubernetes_node"),
			DataConverterUtil.getStringOrNull(metricData, "pod"),
			DataConverterUtil.getStringOrNull(metricData, "instance"),
			DataConverterUtil.getStringOrNull(metricData, "modelName"),
			DataConverterUtil.getStringOrNull(metricData, "gpu"),
			DataConverterUtil.getStringOrNull(metricData, "resource"),
			String.valueOf(value)
		);
	}

	private ReportDTO.ResourceDTO createResourceDTO(JsonNode result, String resourceName) {
		// 결과 값 추출
		JsonNode values = result.get(0).path("values");

		return new ReportDTO.ResourceDTO().builder()
			.resourceName(resourceName)
			.valueDTOS(createReportValue(values))
			.build();
	}

	/**
	 * 조회될 Promql 조회하는 메소드
	 * @param requestDTO pod, node, namespace 정보가 담긴 객체
	 * @return 조회된 Promql
	 */
	private String getPromql(RequestDTO requestDTO) {
		try {
			// Promql 생성
			return createPromql(Promql.valueOf(requestDTO.metricName()), requestDTO);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(CommonErrorCode.MONITOR_METRIC_NOT_FOUND);
		}
	}
	/**
	 * 조회된 Prometheus History Metrics 추출하여 HistoryDTO List 반환하는 메소드
	 *
	 * @param jsonResponse 조회된 Metric 객체
	 * @param metricName Metric 이름
	 * @return 반환될 HistoryDTO List
	 */
	public List<ResponseDTO.HistoryDTO> extractHistoryMetrics(String jsonResponse, String metricName) {
		List<ResponseDTO.HistoryDTO> responseDTOS = new ArrayList<>();

		try {
			// JSON 파싱을 위한 ObjectMapper 생성
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonResponse);

			// result 필드 추출
			JsonNode results = jsonNode.path("data").path("result");
			for (JsonNode result : results) {
				// 리스트에 추가
				responseDTOS.add(createHistoryDTO(result, metricName));
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
	 * @return 생성된 ResponseDTO
	 */
	private ResponseDTO.HistoryDTO createHistoryDTO(JsonNode result, String metric) {
		JsonNode metricData = result.path("metric");

		// 결과 값 추출
		JsonNode values = result.path("values");
		// ResponseDTO 객체 생성하여 반환
		return ResponseDTO.HistoryDTO.builder()
			.nameSpace(DataConverterUtil.getStringOrNull(metricData, "namespace"))
			.instance(DataConverterUtil.getStringOrNull(metricData, "instance"))
			.metricName(metric)
			.kubeNodeName(DataConverterUtil.getStringOrNull(metricData, "kubernetes_node"))
			.gpuIndex(DataConverterUtil.getStringOrNull(metricData, "gpu"))
			.modelName(DataConverterUtil.getStringOrNull(metricData, "modelName"))
			.podName(DataConverterUtil.getStringOrNull(metricData, "pod"))
			.nodeName(DataConverterUtil.getStringOrNull(metricData, "node"))
			.prettyName(DataConverterUtil.getStringOrNull(metricData, "pretty_name"))
			.internalIp(DataConverterUtil.getStringOrNull(metricData, "internal_ip"))
			.valueDTOS(createHistoryValue(values))
			.build();
	}
	/**
	 * 과거 Values 생성 메소드
	 * @param values 조회된 Value
	 * @return 생성된 ValueDTO List
	 */
	private List<ResponseDTO.ValueDTO> createHistoryValue(JsonNode values) {
		List<ResponseDTO.ValueDTO> valueDTOList = new ArrayList<>();
		if (values.isArray()) {
			for (JsonNode node : values) {
				// values DTO List에 추가
				valueDTOList.add(ResponseDTO.ValueDTO.builder()
					.dateTime(DataConverterUtil.formatDateTime(node.get(0).asDouble()))
					.value(node.get(1).textValue())
					.build());
			}
		}
		return valueDTOList;
	}
	private List<ReportDTO.ValueDTO> createReportValue(JsonNode values) {
		List<ReportDTO.ValueDTO> valueDTOList = new ArrayList<>();
		if (values.isArray()) {
			for (JsonNode node : values) {
				// values DTO List에 추가
				valueDTOList.add(ReportDTO.ValueDTO.builder()
					.dateTime(DataConverterUtil.formatDateTime(node.get(0).asDouble()))
					.value(node.get(1).textValue())
					.build());
			}
		}
		return valueDTOList;
	}


	/**
	 * Promql 생성하는 메소드
	 * @param promql Client가 요청한 promql
	 * @param requestDTO 조건에 추가될 정보가 담긴 객체
	 * @return 생성된 Promql
	 */
	private String createPromql(Promql promql, RequestDTO requestDTO) {
		String result = "";
		// GPU일 경우 kubernetes_node 사용
		if (promql.getType().equals("GPU")) {
			if (requestDTO.nodeName() != null && !requestDTO.nodeName().isBlank()) {
				result = "kubernetes_node=\"" + requestDTO.nodeName() + "\",";
			}
		}else if(promql.getType().equals("NODE")){
			if (requestDTO.nodeName() != null && !requestDTO.nodeName().isBlank()) {
				result = "node=\"" + requestDTO.nodeName() + "\",";
			}
		}
		else {
			if (requestDTO.namespace() != null && !requestDTO.namespace().isBlank()) {
				result = "namespace=\"" + requestDTO.namespace() + "\",";
			}
		}
		if (requestDTO.podName() != null && !requestDTO.podName().isBlank()) {
			result = result + "pod=~\"" + requestDTO.podName() + ".*\"";
		}
		if(promql.getType().equals("TERMINAL") && Promql.TERMINAL_CPU_UTILIZATION.name().equals(requestDTO.metricName())){
			return String.format(promql.getQuery(), "pod =~\"" + requestDTO.podName() + ".*\"", "node =~ \"" + requestDTO.nodeName() + ".*\"");
		}
		return String.format(promql.getQuery(), result.toLowerCase());
	}
}
