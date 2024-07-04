package com.xiilab.modulemonitor.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ClusterObjectDTO;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.ClusterObject;
import com.xiilab.modulemonitor.enumeration.Promql;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorFacadeService {
	private final K8sMonitorService k8sMonitorService;
	private final PrometheusService prometheusService;

	public List<ResponseDTO.WorkspaceDTO> getWorkspaceResourceList() {

		// GPU 사용량
		String gpuUsageMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_GPU_USAGE.getQuery(), ""));
		// CPU 사용량
		String cpuUsageMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_CPU_USAGE.getQuery(), ""));
		// MEM 사용량
		String memUsageMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_MEM_USAGE.getQuery(), ""));
		// Pending POD
		String wsPendingMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_PENDING_COUNT.getQuery(), ""));
		// ERROR POD
		String wsErrorMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_ERROR_COUNT.getQuery(), ""));
		// Running POD
		String wsRunningMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DASHBOARD_WS_RUNNING_COUNT.getQuery(), ""));

		List<ResponseDTO.RealTimeDTO> gpuUsage = prometheusService.extractMetrics(gpuUsageMetric, "gpuUsage");
		List<ResponseDTO.RealTimeDTO> cpuUsage = prometheusService.extractMetrics(cpuUsageMetric, "cpuUsage");
		List<ResponseDTO.RealTimeDTO> memUsage = prometheusService.extractMetrics(memUsageMetric, "memUsage");
		List<ResponseDTO.RealTimeDTO> pendingCount = prometheusService.extractMetrics(wsPendingMetric, "pendingCount");
		List<ResponseDTO.RealTimeDTO> errorCount = prometheusService.extractMetrics(wsErrorMetric, "wsErrorCount");
		List<ResponseDTO.RealTimeDTO> runnigCount = prometheusService.extractMetrics(wsRunningMetric, "wsRunnigCount");

		gpuUsage.addAll(cpuUsage);
		gpuUsage.addAll(memUsage);
		gpuUsage.addAll(pendingCount);
		gpuUsage.addAll(errorCount);
		gpuUsage.addAll(runnigCount);

		Map<String, List<ResponseDTO.RealTimeDTO>> collect = gpuUsage.stream()
			.collect(Collectors.groupingBy(ResponseDTO.RealTimeDTO::nameSpace));

		List<ResponseDTO.WorkspaceDTO> result = new ArrayList<>();
		for(Map.Entry<String, List<ResponseDTO.RealTimeDTO>> value : collect.entrySet()){
			String wsName = "default";
			double gpu = 0.0;
			double cpu = 0.0;
			double mem = 0.0;
			long running = 0;
			long pending = 0;
			long error = 0;
			wsName = k8sMonitorService.getWorkspaceName(value.getKey());
			for(ResponseDTO.RealTimeDTO realTimeDTO : value.getValue()){
				switch (realTimeDTO.metricName()){
					case "gpuUsage" -> gpu = Double.parseDouble(realTimeDTO.value());
					case "cpuUsage" -> cpu = Double.parseDouble(realTimeDTO.value());
					case "memUsage" -> mem = Double.parseDouble(realTimeDTO.value());
					case "wsRunnigCount" -> running = (long)Double.parseDouble(realTimeDTO.value());
					case "pendingCount" -> pending = (long)Double.parseDouble(realTimeDTO.value());
					case "wsErrorCount" -> error = (long)Double.parseDouble(realTimeDTO.value());
				}
			}
			result.add(ResponseDTO.WorkspaceDTO.builder()
				.workspaceResourceName(value.getKey())
				.workspaceName(wsName)
				.gpuUsage(gpu)
				.cpuUsage(cpu)
				.memUsage(mem)
				.wlRunningCount(running)
				.pendingCount(pending)
				.errorCount(error)
				.build());
		}

		return result;
	}

	public ResponseDTO.NodeResourceDTO getNodeResource(String nodeName) {
		String node = "node=" + "\"" + nodeName  + "\"";
		// CPU 총량
		String cpuMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_CPU_USAGE.getQuery(), node));
		String cpuResponse = DataConverterUtil.formatObjectMapper(cpuMetric);
		String instance = DataConverterUtil.getInstance(cpuMetric);
		String memMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.REPORT_CLUSTER_MEM_USAGE.getQuery(), node));

		String memResponse = DataConverterUtil.formatObjectMapper(memMetric);

		ResponseDTO.ResponseClusterDTO clusterCPU = k8sMonitorService.getDashboardClusterCPU(nodeName,
			DataConverterUtil.formatRoundTo(cpuResponse));

		ResponseDTO.ResponseClusterDTO clusterMEM = k8sMonitorService.getDashboardClusterMemByNode(nodeName, memResponse);
		// GPU
		ResponseDTO.ResponseClusterDTO clusterGPU = k8sMonitorService.getDashboardClusterGPU(nodeName);

		String diskTotal = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_TOTAL_DISK_SIZE_BYTE.getQuery(), node));
		String diskUsage = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_USAGE_DISK_SIZE_BYTE.getQuery(), node));

		String diskTotalByte = DataConverterUtil.formatObjectMapper(diskTotal);
		String diskUsageByte = DataConverterUtil.formatObjectMapper(diskUsage);

		return ResponseDTO.NodeResourceDTO.builder()
			.nodeName(nodeName)
			.instance(instance)
			.cpuTotal(clusterCPU.total())
			.cpuRequest(clusterCPU.cpuRequest())
			.cpuUsage(clusterCPU.cpuUsage())
			.memTotal(clusterMEM.total())
			.memRequest(clusterMEM.request())
			.memUsage(clusterMEM.usage())
			.gpuTotal(clusterGPU.total())
			.gpuUsage(clusterGPU.usage())
			.diskTotal(Long.parseLong(diskTotalByte))
			.diskUsage(Long.parseLong(diskUsageByte))
			.build();
	}

	public List<ResponseDTO.ResponseClusterDTO> getDashboardCluster() {
		String cpuMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_CPU_USAGE.getQuery(), ""));
		String cpuResponse = DataConverterUtil.formatObjectMapper(cpuMetric);
		String memMetric = prometheusService.getRealTimeMetricByQuery(Promql.NODE_MEM_USAGE_KI.getQuery());
		String memResponse = DataConverterUtil.formatObjectMapper(memMetric);

		return List.of(
			// CPU
			k8sMonitorService.getDashboardClusterCPU("", DataConverterUtil.formatRoundTo(cpuResponse)),
			// MEM
			k8sMonitorService.getDashboardClusterMEM("", memResponse),
			// GPU
			k8sMonitorService.getDashboardClusterGPU(""),
			getDashboardClusterDISK()
		);
	}

	/**
	 * 워크스페이스별 GPU, CPU등의 자원 사용량 리스트를 조회하는 메소드
	 * @param gpuMetric GPU 사용량 Metric
	 * @param cpuMetric CPU 사용량 Metric
	 * @param memMetric MEM 사용량 Metric
	 * @param wlPendingMetric    Workload Pending Metric
	 * @return mapping된 Disk Space List
	 */
	private List<ResponseDTO.WorkspaceDTO> mapToWorkspaceDTO(String gpuMetric, String cpuMetric, String memMetric,
		String memQuotaMetric, String wlPendingMetric, String wlRunningMetric) {
		List<ResponseDTO.WorkspaceDTO> workspaceDTOList = new ArrayList<>();

		Iterator<JsonNode> gpuIterator = DataConverterUtil.formatJsonNode(gpuMetric);
		Iterator<JsonNode> cpuSizesIterator = DataConverterUtil.formatJsonNode(cpuMetric);
		Iterator<JsonNode> memSizesIterator = DataConverterUtil.formatJsonNode(memMetric);
		Iterator<JsonNode> memQuotaSizesIterator = DataConverterUtil.formatJsonNode(memQuotaMetric);
		Iterator<JsonNode> wlPendingSizesIterator = DataConverterUtil.formatJsonNode(wlPendingMetric);
		Iterator<JsonNode> wlRunningSizesIterator = DataConverterUtil.formatJsonNode(wlRunningMetric);

		while (gpuIterator.hasNext() && cpuSizesIterator.hasNext() && memSizesIterator.hasNext()
			&& memQuotaSizesIterator.hasNext() && wlPendingSizesIterator.hasNext() && wlRunningSizesIterator.hasNext()) {
			JsonNode gpuResult = gpuIterator.next();
			JsonNode cpuResult = cpuSizesIterator.next();
			JsonNode memResult = memSizesIterator.next();
			JsonNode memQuotaResult = memQuotaSizesIterator.next();
			JsonNode wlPendingResult = wlPendingSizesIterator.next();
			JsonNode wlRunningResult = wlRunningSizesIterator.next();

			double gpu = DataConverterUtil.formatRoundTo(gpuResult.get("value").get(1).asText());
			double cpu = DataConverterUtil.formatRoundTo(cpuResult.get("value").get(1).asText());
			double memQuota = DataConverterUtil.formatRoundTo(memQuotaResult.get("value").get(1).asText());
			double mem = DataConverterUtil.formatRoundTo(memResult.get("value").get(1).asText());
			long wlPending = Long.parseLong(wlPendingResult.get("value").get(1).asText());
			long wlRunning = Long.parseLong(wlRunningResult.get("value").get(1).asText());

			String nameSpace = gpuResult.get("metric").get("namespace").asText();

			// 해당 워크스페이스의 워크로드 카운트
			long wlCount = k8sMonitorService.getWorkloadCountByNamespace(nameSpace);
			// 워크스페이스에서 발생한 에러 카운트
			long workspaceErrorCount = k8sMonitorService.getWorkloadErrorCount(nameSpace);
			// 워크스페이스 ResourceName
			String workspaceName = k8sMonitorService.getWorkspaceName(nameSpace);
			// diskDTO List 추가
			workspaceDTOList.add(
				ResponseDTO.WorkspaceDTO.builder()
					.workspaceResourceName(nameSpace)
					.workspaceName(workspaceName)
					.gpuUsage(gpu)
					.cpuUsage(cpu)
					.memUsage((mem/ memQuota ) * 100)
					.wlRunningCount(wlRunning)
					.wlCount(wlCount)
					.errorCount(workspaceErrorCount)
					.pendingCount(wlPending)
					.build()
			);
		}
		return workspaceDTOList;
	}

	public ResponseDTO.ResponseClusterDTO getDashboardClusterDISK() {
		String diskTotal = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.TOTAL_NODE_DISK_SIZE_BYTES.getQuery(), ""));
		String diskUsage = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.USAGE_NODE_DISK_SIZE_BYTES.getQuery(), ""));

		String diskTotalByte = DataConverterUtil.formatObjectMapper(diskTotal);
		String diskUsageByte = DataConverterUtil.formatObjectMapper(diskUsage);

		return ResponseDTO.ResponseClusterDTO.builder()
			.name("DISK")
			.total(Long.parseLong(diskTotalByte))
			.usage(Long.parseLong(diskUsageByte))
			.build();
	}

	public ResponseDTO.ClusterResourceDTO getClusterResource() {
		String nodeCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_COUNT.getQuery(), ""));
		String daemonsetCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DAEMONSET_COUNT.getQuery(), ""));
		String podCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.POD_COUNT.getQuery(), ""));
		String volumeCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.VOLUME_COUNT.getQuery(), ""));
		String deploymentCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DEPLOYMENT_COUNT.getQuery(), ""));
		String serviceCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.SERVICE_COUNT.getQuery(), ""));
		String containerCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.CONTAINER_COUNT.getQuery(), ""));
		String namespaceCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NAMESPACE_COUNT.getQuery(), ""));
		String statefulsetCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.STATEFULSET_COUNT.getQuery(), ""));
		String hpaCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.HPA_COUNT.getQuery(), ""));

		return ResponseDTO.ClusterResourceDTO.builder()
			.nodeCount(DataConverterUtil.formatObjectMapper(nodeCount))
			.daemonsetCount(DataConverterUtil.formatObjectMapper(daemonsetCount))
			.podCount(DataConverterUtil.formatObjectMapper(podCount))
			.persistentVolumeCount(DataConverterUtil.formatObjectMapper(volumeCount))
			.deploymentsCount(DataConverterUtil.formatObjectMapper(deploymentCount))
			.serviceCount(DataConverterUtil.formatObjectMapper(serviceCount))
			.containerCount(DataConverterUtil.formatObjectMapper(containerCount))
			.namespaceCount(DataConverterUtil.formatObjectMapper(namespaceCount))
			.statefulsetCount(DataConverterUtil.formatObjectMapper(statefulsetCount))
			.hpaCount(DataConverterUtil.formatObjectMapper(hpaCount))
			.build();
	}

	public ResponseDTO.ClusterObjectDTO getClusterObject() {
		String podRunning = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.POD_RUNNING_PERCENT.getQuery(), ""));
		String podPendingCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.POD_PENDING_COUNT.getQuery(), ""));
		String podFailCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.POD_FAIL_COUNT.getQuery(), ""));
		String nodeReady = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_READY_PERCENT.getQuery(), ""));
		String deploymentUnhealyhy = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DEPLOYMENT_UNHEALYHY_COUNT.getQuery(), ""));
		String daemonsetUnhealyhy = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.DAEMONSET_UNHEALYHY_COUNT.getQuery(), ""));
		String hpaUnhealyhy = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.HPA_UNHEALYHY_COUNT.getQuery(), ""));
		String statefulsetUnhealyhy = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.STATEFULSET_UNHEALYHY_COUNT.getQuery(), ""));
		String containerRestart = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.CONTAINER_RESTART_COUNT.getQuery(), ""));
		String imagePullBackoffCount = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.CONTAINER_IMAGE_PULL_BACK_OFF_COUNT.getQuery(), ""));

		String podRunningPercent = DataConverterUtil.formatObjectMapper(podRunning);

		return ResponseDTO.ClusterObjectDTO.builder()
			.podRuning(DataConverterUtil.roundToString(podRunningPercent))
			.pendingPodCount(DataConverterUtil.formatObjectMapper(podPendingCount))
			.failPodCount(DataConverterUtil.formatObjectMapper(podFailCount))
			.nodeReady(DataConverterUtil.formatObjectMapper(nodeReady))
			.unHealthyDeployments(DataConverterUtil.formatObjectMapper(deploymentUnhealyhy))
			.unHealthyDaemonSet(DataConverterUtil.formatObjectMapper(daemonsetUnhealyhy))
			.unHealthyHPA(DataConverterUtil.formatObjectMapper(hpaUnhealyhy))
			.unHealthyStatefulset(DataConverterUtil.formatObjectMapper(statefulsetUnhealyhy))
			.containerRestart(DataConverterUtil.formatObjectMapper(containerRestart))
			.containerImageRestart(DataConverterUtil.formatObjectMapper(imagePullBackoffCount))
			.build();
	}

	public Map<String, Map<String, Long>> getClusterPendingCount(long minute) {
		// Cluster Pending History 조회 Promql
		String promql = Promql.POD_PENDING.getQuery();
		// 검색 기간
		String endDate = DataConverterUtil.getCurrentUnixTime();
		String startDate = DataConverterUtil.subtractMinutesFromCurrentTime(minute);

		// Pending History 조회
		List<ResponseDTO.HistoryDTO> historyMetricByQuery = prometheusService.getHistoryMetricByQuery(promql, startDate, endDate);

		List<ResponseDTO.ClusterPendingDTO> clusterPendingDTOList = new ArrayList<>();

		for (ResponseDTO.HistoryDTO historyDTO : historyMetricByQuery) {
			String nodeName = k8sMonitorService.getNodeName(historyDTO.podName(), historyDTO.nameSpace());
			for (ResponseDTO.ValueDTO valueDTO : historyDTO.valueDTOS()) {
				clusterPendingDTOList.add(ResponseDTO.ClusterPendingDTO.builder()
					.date(valueDTO.dateTime())
					.value(valueDTO.value())
					.podName(historyDTO.podName())
					.nodeName(nodeName)
					.build());
			}
		}
		return clusterPendingDTOList.stream()
			.collect(Collectors.groupingBy(ResponseDTO.ClusterPendingDTO::date,
				Collectors.groupingBy(ResponseDTO.ClusterPendingDTO::nodeName, Collectors.counting())))
			.entrySet().stream()
			.sorted(Map.Entry.comparingByKey()) // 시간별로 정렬
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
	
	public Map<String, Map<String, Long>> getClusterContainerRestart(long minute){
		// Cluster Pending History 조회 Promql
		String promql = Promql.CONTAINER_RESTART.getQuery();
		// 검색 기간
		String endDate = DataConverterUtil.getCurrentUnixTime();
		String startDate = DataConverterUtil.subtractMinutesFromCurrentTime(minute);

		// Pending History 조회
		List<ResponseDTO.HistoryDTO> historyMetricByQuery = prometheusService.getHistoryMetricByQuery(promql, startDate,
			endDate);

		List<ResponseDTO.ClusterPendingDTO> clusterPendingDTOList = new ArrayList<>();
		for (ResponseDTO.HistoryDTO historyDTO : historyMetricByQuery) {
			for (ResponseDTO.ValueDTO valueDTO : historyDTO.valueDTOS()) {
				clusterPendingDTOList.add(ResponseDTO.ClusterPendingDTO.builder()
					.date(valueDTO.dateTime())
					.value(valueDTO.value())
					.podName(historyDTO.podName())
					.namespace(historyDTO.nameSpace())
					.build());
			}
		}

		return clusterPendingDTOList.stream()
			.collect(Collectors.groupingBy(ResponseDTO.ClusterPendingDTO::date,
				Collectors.groupingBy(ResponseDTO.ClusterPendingDTO::namespace, Collectors.counting())))
			.entrySet().stream()
			.sorted(Map.Entry.comparingByKey()) // 시간별로 정렬
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	public List<ResponseDTO.ClusterPodInfo> getClusterPendingAndFailPod(){
		// Cluster Pending History 조회 Promql
		String promql = Promql.POD_PENDING_FAIL_INFO.getQuery();
		String realTimeMetricByQuery = prometheusService.getRealTimeMetricByQuery(promql);
		// Promql 조회
		List<ResponseDTO.RealTimeDTO> realTimeDTOS = prometheusService.extractMetrics(realTimeMetricByQuery, "");

		List<ResponseDTO.ClusterPodInfo> result = new ArrayList<>();
		for(ResponseDTO.RealTimeDTO realTimeDTO : realTimeDTOS){
			result.add(k8sMonitorService.getClusterPendingAndFailPod(realTimeDTO.podName(), realTimeDTO.nameSpace()));
		}

		return result;
	}

	public List<ClusterObjectDTO> getClusterObjectByObject(ClusterObject clusterObject){

		return switch (clusterObject){
			case POD_RUNNING ->
				k8sMonitorService.getClusterRunningPods();
			case POD_PENDING ->
				k8sMonitorService.getClusterPendingPods();
			case POD_FAILED ->
				k8sMonitorService.getClusterFailPods();
			case NODE_READY ->
				k8sMonitorService.getReadyNodes();
			case UNHEALTHY_DEPOLYMENTS ->
				k8sMonitorService.getUnhealthyDeployments();
			case UNHEALTHY_HPA ->
				k8sMonitorService.getUnhealthyHpas();
			case UNHEALTHY_DAEMONSET ->
				k8sMonitorService.getUnhealthyDaemonSets();
			case UNHEALTHY_STATEFULSET ->
				k8sMonitorService.getUnhealthyStatefulSets();
			case CONTAINER_RESTART ->
				k8sMonitorService.getContainerRestart();
			case CONTAINER_IMAGE_RESTART ->
				k8sMonitorService.getContainerImageRestart();
		};
	}

	public List<ResponseDTO.HistoryDTO> getMultiCPUUtilization(RequestDTO requestDTO) {
		List<ResponseDTO.HistoryDTO> historyMetric = prometheusService.getHistoryMetric(requestDTO);

		// Process the list to divide each value by 3
		return historyMetric.stream()
			.map(historyDTO -> {
				Long cpuCore = k8sMonitorService.getCpuCore(historyDTO.nodeName());
				return new ResponseDTO.HistoryDTO(
					historyDTO.metricName(),
					historyDTO.nameSpace(),
					historyDTO.internalIp(),
					historyDTO.nodeName(),
					historyDTO.podName(),
					historyDTO.kubeNodeName(),
					historyDTO.modelName(),
					historyDTO.gpuIndex(),
					historyDTO.instance(),
					historyDTO.prettyName(),
					historyDTO.valueDTOS().stream()
						.map(valueDTO ->
							new ResponseDTO.ValueDTO(valueDTO.dateTime(),
								String.valueOf(Double.parseDouble(valueDTO.value()) / cpuCore)))
						.toList());
			})
			.toList();
	}
}
