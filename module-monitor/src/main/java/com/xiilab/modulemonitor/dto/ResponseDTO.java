package com.xiilab.modulemonitor.dto;

import java.util.List;

import lombok.Builder;

public record ResponseDTO() {

	/**
	 * 실시간 데이터
	 */
	@Builder
	public record RealTimeDTO(String metricName,
							  String dateTime,
							  String nameSpace,
							  String nodeName,
							  String kubeNodeName,
							  String podName,
							  String instance,
							  String modelName,
							  String gpuIndex,
							  String resource,
							  String value,
							  String container) {
	}

	/**
	 * 과거 데이터
	 */
	@Builder
	public record HistoryDTO(String metricName,
							 String nameSpace,
							 String internalIp,
							 String nodeName,
							 String podName,
							 String kubeNodeName,
							 String modelName,
							 String gpuIndex,
							 String instance,
							 String prettyName,
							 List<ValueDTO> valueDTOS
	) {

	}

	@Builder
	public record RerportNodeUsageDTO(
		String metricName,
		String nodeName,
		List<ReportNodeUsageValueDTO> values
	) {
	}

	@Builder
	public record ReportNodeUsageValueDTO(
		String gpuIndex,
		String date,
		String value
	) {

	}

	/**
	 * 과거 데이터 Value DTO
	 */
	@Builder
	public record ValueDTO(String dateTime,
						   String value) {
	}

	/**
	 * Promql
	 */
	@Builder
	public record PromqlDTO(String metricName,
							String description,
							String type
	) {
	}

	/**
	 * 워크로드에서 발생된 Event를 위한 DTO
	 */
	@Builder
	public record EventDTO(String type,
						   String workloadName,
						   String time,
						   String reason,
						   String message) {
	}

	/**
	 * 대시보드 워크스페이스 DTO
	 */
	@Builder
	public record WorkspaceDTO(String workspaceResourceName,
							   String workspaceName,
							   double gpuUsage,
							   double cpuUsage,
							   double memUsage,
							   long wlRunningCount,
							   long wlCount,
							   long pendingCount,
							   long errorCount) {
	}

	/**
	 * 대시보드 노드 자원 사용량 DTO
	 */
	@Builder
	public record NodeResourceDTO(String nodeName,
								  String instance,
								  long cpuTotal,
								  double cpuRequest,
								  double cpuUsage,
								  long memTotal,
								  long memRequest,
								  long memUsage,
								  long gpuTotal,
								  long gpuUsage,
								  long migTotal,
								  long migUsage,
								  long diskTotal,
								  long diskUsage) {
	}

	/**
	 * 대시보드 노드 상태값 리스트 DTO
	 */
	@Builder
	public record NodeResponseDTO(String nodeName,
								  String status,
								  String ip) {
	}

	/**
	 * 대시보드 워크로드 상태값 리스트
	 */
	@Builder
	public record WorkloadResponseDTO(String wlName,
									  String wsName,
									  String status) {
	}

	/**
	 * 워크스페이스 대시보드
	 */
	@Builder
	public record WorkspaceResponseDTO(String wsName,
									   String gpuUsed,
									   String gpuHard,
									   String cpuUsed,
									   String cpuHard,
									   String memUsed,
									   String memHard,
									   List<WorkloadResponseDTO> workloadResponseDTOS) {
	}

	@Builder
	public record ResponseClusterDTO(String name,
									 long total,
									 double cpuRequest,
									 long request,
									 double cpuUsage,
									 long usage) {
	}

	@Builder
	public record ResponseClusterResourceDTO(int cpu,
											 int mem,
											 int gpu) {
	}

	@Builder
	public record ClusterResourceDTO(String nodeCount,
									 String daemonsetCount,
									 String podCount,
									 String persistentVolumeCount,
									 String deploymentsCount,
									 String serviceCount,
									 String containerCount,
									 String namespaceCount,
									 String statefulsetCount,
									 String hpaCount) {
	}

	@Builder
	public record ClusterObjectDTO(String podRuning,
								   String nodeReady,
								   String pendingPodCount,
								   String failPodCount,
								   String unHealthyDeployments,
								   String unHealthyDaemonSet,
								   String unHealthyHPA,
								   String unHealthyStatefulset,
								   String containerRestart,
								   String containerImageRestart) {
	}

	@Builder
	public record ClusterReasonDTO(long lastSEEN,
								   String time,
								   String reason) {
	}

	@Builder
	public record ClusterPendingDTO(String date,
									String podName,
									String nodeName,
									String namespace,
									String value) {
	}

	@Builder
	public record ClusterPodInfo(String podName,
								 String nodeName,
								 String status,
								 String reason) {
	}

}
