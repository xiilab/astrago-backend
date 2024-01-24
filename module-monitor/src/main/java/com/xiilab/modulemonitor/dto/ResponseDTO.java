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
							  String value) {
	}

	/**
	 * 과거 데이터
	 */
	@Builder
	public record HistoryDTO(String metricName,
							 String nameSpace,
							 String nodeName,
							 String podName,
							 String instance,
							 List<ValueDTO> valueDTOS
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
	public record WorkspaceDTO(String workspaceName,
							   double gpuUsage,
							   double cpuUsage,
							   double memUsage,
							   long wlCount,
							   long pendingCount,
							   long errorCount) {
	}

	/**
	 * 대시보드 노드 자원 사용량 DTO
	 */
	@Builder
	public record NodeResourceDTO(String nodeName,
								  double gpuUsage,
								  double cpuUsage,
								  double diskUsage,
								  double memUsage) {
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
}
