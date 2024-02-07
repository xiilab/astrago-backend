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
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.DataConverterUtil;
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
		return extractMetrics(realTimeMetricByQuery, requestDTO.metricName());

	}

	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO) {
		// 검색시간 UnixTime로 변환
		String startDate = DataConverterUtil.toUnixTime(requestDTO.startDate());
		String endDate = DataConverterUtil.toUnixTime(requestDTO.endDate());
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
			throw new RestApiException(CommonErrorCode.MONITOR_METRIC_NOT_FOUND);
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
	 * @param metricName Metric 이름
	 * @return 반환될 ResponseDTO List
	 */
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
			.nameSpace(DataConverterUtil.getStringOrNull(metricData, "namespace"))
			.instance(DataConverterUtil.getStringOrNull(metricData, "instance"))
			.metricName(metric)
			.podName(DataConverterUtil.getStringOrNull(metricData, "pod"))
			.nodeName(DataConverterUtil.getStringOrNull(metricData, "node"))
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
	 * 워크스페이스별 자원자원 사용량 리스트 조회 메소드
	 * @return 조회된 워크스페이스 리스트
	 */
	public List<ResponseDTO.WorkspaceDTO> getWorkspaceResourceList(){
		// GPU Metric 조회
		String gpuMetric = prometheus.getRealTimeMetricByQuery(String.format(Promql.WS_GPU_USAGE.getQuery(), ""));
		// CPU Metric 조회
		String cpuMetric = prometheus.getRealTimeMetricByQuery(String.format(Promql.WS_CPU_USAGE.getQuery(), ""));
		// MEM Metric 조회
		String memMetric = prometheus.getRealTimeMetricByQuery(String.format(Promql.WS_MEM_USAGE.getQuery(), ""));
		String wlPendingMetric = prometheus.getRealTimeMetricByQuery(String.format(Promql.WL_PENDING_COUNT.getQuery(), ""));

		return mapToWorkspaceDTO(gpuMetric, cpuMetric, memMetric, wlPendingMetric);
	}

	/**
	 * 노드별 GPU, CPU, DISK, MEM 사용량 조회하는 메소드
	 * @param nodeName 조회될 nodeName
	 * @return 조회된 GPU, DISK, CPU, MEM 사용량
	 */
	public ResponseDTO.NodeResourceDTO getNodeResource(String nodeName){
		String result = "";
		if(nodeName != null){
			result = "kubernetes_node = \"" + nodeName + "\"";
		}
		// GPU
		String gpuMetric = prometheus.getRealTimeMetricByQuery(
			String.format(Promql.GPU_USAGE.getQuery(), result));
		// MEM
		String memMetric = prometheus.getRealTimeMetricByQuery(
			String.format(Promql.GPU_MEM_USAGE.getQuery(), result, result, result));
		if(nodeName != null){
			result = "node = \"" + nodeName + "\"";
		}
		// CPU
		String cpuMetric = prometheus.getRealTimeMetricByQuery(
			String.format(Promql.CPU_USAGE.getQuery(), result));
		// DISK
		String diskUsage = prometheus.getRealTimeMetricByQuery(
			String.format(Promql.NODE_DISK_USAGE.getQuery(), result, result, result));

		return mapToNodeResourceDTO(gpuMetric, memMetric, cpuMetric, diskUsage, nodeName);
	}

	/**
	 * 대시보드 노드 리스트 출력 메소드
	 * @return
	 */
	public List<ResponseDTO.NodeResponseDTO> getNodeList(){
		return k8sMonitorService.getNodeList();
	}

	/**
	 * 대시보드 워크로드 리스트 출력 메소드
	 * @return
	 */
	public List<ResponseDTO.WorkloadResponseDTO> getWlList(){
		return k8sMonitorService.getWlList();
	}

	private ResponseDTO.NodeResourceDTO mapToNodeResourceDTO(String gpuMetric, String memMetric, String cpuMetric, String diskUsage, String nodeName){
		// gpu 사용량 매핑
		String gpuResult = DataConverterUtil.formatObjectMapper(gpuMetric);
		// cpu 사용량 매핑
		String cpuResult = DataConverterUtil.formatObjectMapper(cpuMetric);
		// mem 사용량 매핑
		String memResult = DataConverterUtil.formatObjectMapper(memMetric);
		// disk 사용량 매핑
		String diskResult = DataConverterUtil.formatObjectMapper(diskUsage);

		return ResponseDTO.NodeResourceDTO.builder()
			.nodeName(nodeName)
			.gpuUsage(DataConverterUtil.formatRoundTo(gpuResult))
			.cpuUsage(DataConverterUtil.formatRoundTo(cpuResult))
			.memUsage(DataConverterUtil.formatRoundTo(memResult))
			.diskUsage(DataConverterUtil.formatRoundTo(diskResult))
			.build();
	}

	/**
	 * 워크스페이스별 GPU, CPU등의 자원 사용량 리스트를 조회하는 메소드
	 * @param gpuMetric GPU 사용량 Metric
	 * @param cpuMetric CPU 사용량 Metric
	 * @param memMetric MEM 사용량 Metric
	 * @param wlPendingMetric	Workload Pending Metric
	 * @return mapping된 Disk Space List
	 */
	private List<ResponseDTO.WorkspaceDTO> mapToWorkspaceDTO(String gpuMetric, String cpuMetric, String memMetric, String wlPendingMetric){
		List<ResponseDTO.WorkspaceDTO> workspaceDTOList = new ArrayList<>();

		Iterator<JsonNode> gpuIterator = DataConverterUtil.formatJsonNode(gpuMetric);
		Iterator<JsonNode> cpuSizesIterator = DataConverterUtil.formatJsonNode(cpuMetric);
		Iterator<JsonNode> memSizesIterator = DataConverterUtil.formatJsonNode(memMetric);
		Iterator<JsonNode> wlPendingSizesIterator = DataConverterUtil.formatJsonNode(wlPendingMetric);

		while (gpuIterator.hasNext() && cpuSizesIterator.hasNext() && memSizesIterator.hasNext() && wlPendingSizesIterator.hasNext()) {
			JsonNode gpuResult = gpuIterator.next();
			JsonNode cpuResult = cpuSizesIterator.next();
			JsonNode memResult = memSizesIterator.next();
			JsonNode wlPendingResult = wlPendingSizesIterator.next();

			double gpu = DataConverterUtil.formatRoundTo(gpuResult.get("value").get(1).asText());
			double cpu = DataConverterUtil.formatRoundTo(cpuResult.get("value").get(1).asText());
			double mem = DataConverterUtil.formatRoundTo(memResult.get("value").get(1).asText());
			long wlPending = Long.parseLong(wlPendingResult.get("value").get(1).asText());

			String nameSpace = gpuResult.get("metric").get("namespace").asText();

			// 해당 워크스페이스의 워크로드 카운트
			long wlCount = k8sMonitorService.getWorkloadCountByNamespace(nameSpace);
			// 워크스페이스에서 발생한 에러 카운트
			long workspaceErrorCount = k8sMonitorService.getWorkloadErrorCount(nameSpace);

			// diskDTO List 추가
			workspaceDTOList.add(
				ResponseDTO.WorkspaceDTO.builder()
					.workspaceName(nameSpace)
					.gpuUsage(gpu)
					.cpuUsage(cpu)
					.memUsage(mem)
					.wlCount(wlCount)
					.errorCount(workspaceErrorCount)
					.pendingCount(wlPending)
					.build()
			);
		}
		return workspaceDTOList;
	}

	/**
	 * 해당 WS의 Resource Info 조회 메소드
	 * @param namespace 조회될 WS name
	 * @return CPU,GPU,MEM등의 ResourceQuota, 상태별 워크로드 리스트
	 */
	public ResponseDTO.WorkspaceResponseDTO getWorkspaceResourcesInfo(String namespace){
		return k8sMonitorService.getWlList(namespace);
	}


}

