package com.xiilab.modulemonitor.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorFacadeService {
	private final K8sMonitorService k8sMonitorService;
	private final PrometheusService prometheusService;

	public List<ResponseDTO.WorkspaceDTO> getWorkspaceResourceList() {
		// GPU Metric 조회
		String gpuMetric = prometheusService.getRealTimeMetricByQuery(String.format(Promql.WS_GPU_USAGE.getQuery(), ""));
		// CPU Metric 조회
		String cpuMetric = prometheusService.getRealTimeMetricByQuery(String.format(Promql.WS_CPU_USAGE.getQuery(), ""));
		// MEM Metric 조회
		String memMetric = prometheusService.getRealTimeMetricByQuery(String.format(Promql.WS_MEM_USAGE.getQuery(), ""));
		String wlPendingMetric = prometheusService.getRealTimeMetricByQuery(String.format(Promql.WL_PENDING_COUNT.getQuery(), ""));

		return mapToWorkspaceDTO(gpuMetric, cpuMetric, memMetric, wlPendingMetric);
	}

	public ResponseDTO.NodeResourceDTO getNodeResource(String nodeName) {
		String result = "";
		if(nodeName != null){
			result = "kubernetes_node = \"" + nodeName + "\"";
		}
		// GPU
		String gpuMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.GPU_USAGE.getQuery(), result));
		if(nodeName != null){
			result = "node = \"" + nodeName + "\"";
		}
		// MEM
		String memMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_MEM_USAGE.getQuery(), result, result, result));
		// CPU
		String cpuMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.CPU_USAGE.getQuery(), result));
		// DISK
		String diskUsage = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.NODE_DISK_USAGE.getQuery(), result, result, result));

		return mapToNodeResourceDTO(gpuMetric, memMetric, cpuMetric, diskUsage, nodeName);
	}

	public List<ResponseDTO.ResponseClusterDTO> getDashboardCluster() {
		String cpuMetric = prometheusService.getRealTimeMetricByQuery(
			String.format(Promql.CPU_USAGE.getQuery(), ""));
		String cpuResponse = DataConverterUtil.formatObjectMapper(cpuMetric);
		String memMetric = prometheusService.getRealTimeMetricByQuery(Promql.NODE_MEM_USAGE_KI.getQuery());
		String memResponse = DataConverterUtil.formatObjectMapper(memMetric);
		return List.of(
			// CPU
			k8sMonitorService.getDashboardClusterCPU("", DataConverterUtil.formatRoundTo(cpuResponse)),
			// MEM
			k8sMonitorService.getDashboardClusterMEM("", memResponse),

			k8sMonitorService.getDashboardClusterGPU(""),
			getDashboardClusterDISK()
		);
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
	private ResponseDTO.NodeResourceDTO mapToNodeResourceDTO(String gpuMetric, String memMetric, String cpuMetric, String diskUsage, String nodeName){
		// cpu 사용량 매핑
		String cpuResult = DataConverterUtil.formatObjectMapper(cpuMetric);
		// gpu 사용량 매핑
		String gpuResult = DataConverterUtil.formatObjectMapper(gpuMetric);
		// mem 사용량 매핑
		String memResult = DataConverterUtil.formatObjectMapper(memMetric);
		// disk 사용량 매핑
		String diskResult = DataConverterUtil.formatObjectMapper(diskUsage);
		// CPU 총사이즈 및 요청량 조회
		ResponseDTO.ResponseClusterDTO clusterCPU = k8sMonitorService.getDashboardClusterCPU(nodeName, DataConverterUtil.formatRoundTo(cpuResult));
		// MEM 총사이즈 및 요청량 조회
		ResponseDTO.ResponseClusterDTO clusterMEM = k8sMonitorService.getDashboardClusterMEM(nodeName, memResult);

		return ResponseDTO.NodeResourceDTO.builder()
			.nodeName(nodeName)
			// CPU
			.cpuTotal(clusterCPU.total())
			.cpuRequest(clusterCPU.request())
			.cpuUsage(DataConverterUtil.formatRoundTo(cpuResult))
			// MEM
			.memTotal(clusterMEM.total())
			.memRequest(clusterMEM.request())
			.memUsage(DataConverterUtil.formatRoundTo(memResult))
			.gpuUsage(DataConverterUtil.formatRoundTo(gpuResult))
			.diskUsage(DataConverterUtil.formatRoundTo(diskResult))
			.build();
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
			.total(DataConverterUtil.formatDiskSize(diskTotalByte))
			.usage(DataConverterUtil.formatDiskSize(diskUsageByte))
			.build();
	}
}
