package com.xiilab.servermonitor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.service.DataConverter;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.K8sMonitorService;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorService {
	private final PrometheusService prometheus;
	private final K8sMonitorService k8sMonitorService;
	private final DataConverter common;

	/**
	 * Prometheus 실시간 데이터 조회하는 메소드
	 * @param requestDTO pod, node, namespace 정보가 담긴 객체
	 * @return Prometheus에서 조회된 실시간 데이터 리스트
	 */
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO) {
		// Promql 생성
		String promql = getPromql(requestDTO);
		// Prometheus 조회
		String realTimeMetricByQuery = prometheus.getRealTimeMetricByQuery(promql);
		return extractMetrics(realTimeMetricByQuery, requestDTO);

	}

	/**
	 * Node Error 개수 조회하는 메소드
	 * @return 조회된 Node Error Count
	 */
	public long getNodeErrorCount() {
		return k8sMonitorService.getNodeErrorCount();
	}

	/**
	 * 총사이즈, 사용가능사이즈를 이용한 Disk Space 생성하는 메소드
	 * @param totalSizes 총사이즈
	 * @param availableSizes 사용가능사이즈
	 * @return mapping된 Disk Space List
	 */
	public static List<ResponseDTO.DiskDTO> mapToDiskDTO(String totalSizes, String availableSizes) {
		List<ResponseDTO.DiskDTO> diskDTOList = new ArrayList<>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode totalSizesNode = objectMapper.readTree(totalSizes);
			JsonNode availableSizesNode = objectMapper.readTree(availableSizes);
			// 총사이즈 매핑
			Iterator<JsonNode> totalSizesIterator = totalSizesNode.get("data").get("result").elements();
			// 사용가능 사이즈 매핑
			Iterator<JsonNode> availableSizesIterator = availableSizesNode.get("data").get("result").elements();

			while (totalSizesIterator.hasNext() && availableSizesIterator.hasNext()) {
				JsonNode totalResult = totalSizesIterator.next();
				JsonNode availableResult = availableSizesIterator.next();

				String mountPath = totalResult.get("metric").get("mountpoint").asText();
				String totalSizeStr = formatSize(totalResult.get("value").get(1).asText());
				String availableStr = formatSize(availableResult.get("value").get(1).asText());

				// 사용량, 사용률 게산용
				long totalSize = Long.parseLong(totalResult.get("value").get(1).asText());
				long available = Long.parseLong(availableResult.get("value").get(1).asText());
				// 사용량
				long used = totalSize - available;
				// 사용률
				double usage = ((double)used / totalSize) * 100;
				// diskDTO List 추가
				diskDTOList.add(ResponseDTO.DiskDTO.builder()
						.mountPath(mountPath)
						.size(totalSizeStr)
						.available(availableStr)
						.used(formatSize(String.valueOf(used)))
						.usage(String.format("%.2f", usage))
					.build());
			}
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Disk Space 조회 실패하였습니다.");
		}

		return diskDTOList;
	}

	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO) {
		// 검색시간 UnixTime로 변환
		String startDate = common.toUnixTime(requestDTO.startDate());
		String endDate = common.toUnixTime(requestDTO.endDate());
		// Promql 생성
		String promql = getPromql(requestDTO);
		String result = prometheus.getHistoryMetricByQuery(promql, startDate, endDate);
		return extractHistoryMetrics(result, requestDTO);
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
			throw new IllegalArgumentException("해당 이름의 Metric(" + requestDTO.metricName() + ")이 없습니다.");
		}
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
			if (!requestDTO.nodeName().isBlank()) {
				result = "kubernetes_node=\"" + requestDTO.nodeName() + "\",";
			}
		} else {
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
	 * @return 생성된 ResponseDTO
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
	public List<ResponseDTO.HistoryDTO> extractHistoryMetrics(String jsonResponse, RequestDTO requestDTO) {
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
	 * @return 생성된 ResponseDTO
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
					.dateTime(common.formatDateTime(node.get(0).asDouble()))
					.value(node.get(1).textValue())
					.build());
			}
		}
		return valueDTOList;
	}

	/**
	 * data size 변환 메소드
	 * @param sizeStr 변환될 dataSize
	 * @return 변환된 dataSize
	 */
	private static String formatSize(String sizeStr) {
		long size = Long.parseLong(sizeStr);

		if (size >= 1000000000000L) { // 1 TB
			return String.format("%.2fTB", (double)size / 1000000000000L);
		} else if (size >= 1000000000L) { // 1 GB
			return String.format("%.2fGB", (double)size / 1000000000L);
		} else if (size >= 1000000L) { // 1 MB
			return String.format("%.2fMB", (double)size / 1000000L);
		} else {
			return String.format("%dbytes", size);
		}
	}

	/**
	 * Prometheus Metric List 조회하는 메소드
	 */
	public List<ResponseDTO.PromqlDTO> getPromqlList() {
		return Arrays.stream(Promql.values())
			.map(enumValue -> new ResponseDTO.PromqlDTO(
				enumValue.name(),
				enumValue.getDescription(),
				enumValue.getType()))
			.toList();
	}

	/**
	 * Workload Error Count 조회하는 메소드
	 * @param nameSpace 조회될 NameSpace
	 * @param podName 조회될 PodName
	 * @return 조회된 Workload Error Count
	 */
	public long getWorkloadErrorCount(String nameSpace, String podName) {
		if (StringUtils.hasText(nameSpace) && StringUtils.hasText(podName)) {
			return k8sMonitorService.getWorkloadErrorCount(nameSpace, podName);
		} else if (StringUtils.hasText(nameSpace)) {
			return k8sMonitorService.getWorkloadErrorCount(nameSpace);
		} else {
			return k8sMonitorService.getWorkloadErrorCount();
		}
	}

	/**
	 * K8s에서 발생한 Event List 조회 메소드
	 * @param namespace 조회될 Namespace
	 * @param podName 조회될 pod Name
	 * @return 조회된 Event List
	 */
	public List<ResponseDTO.EventDTO> getEventList(String namespace, String podName) {
		// Namespace, podName 둘다 있는 경우
		if (StringUtils.hasText(namespace) && StringUtils.hasText(podName)) {
			return k8sMonitorService.getEventList(namespace, podName);
			// Namespace 만으로 조회
		} else if (StringUtils.hasText(namespace)) {
			return k8sMonitorService.getEventList(namespace);
			// 조건 없이 조회
		} else {
			return k8sMonitorService.getEventList();
		}
	}

	/**
	 * Disk Space 조회하는 메소드
	 * @param nodeName 조회될 Node Name
	 * @return Mount path별 Disk Space
	 */
	public List<ResponseDTO.DiskDTO> getDiskSpace(String nodeName) {
		String result = "";
		if (StringUtils.hasText(nodeName)) {
			result = "node=\"" + nodeName + "\"";
		}
		// Disk 전체 사이즈 Query Format
		String diskSize = String.format(Promql.NODE_DISK_SIZE.getQuery(), result);
		// Disk 사용 가능 Query Format
		String diskAvail = String.format(Promql.NODE_DISK_USAGE_SIZE.getQuery(), result);
		// Disk 전체 사이즈 Metric 조회
		String totalSize = prometheus.getRealTimeMetricByQuery(diskSize);
		// Disk 사용 가능 Metric 조회
		String availableSize = prometheus.getRealTimeMetricByQuery(diskAvail);

		return mapToDiskDTO(totalSize, availableSize);
	}
}

