package com.xiilab.servermonitor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.servermonitor.service.MonitorService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/monitor")
@RequiredArgsConstructor
public class MonitorController {
	private final MonitorService monitorService;

	/**
	 * 실시간 모니터링 조회 API
	 * @param requestDTO
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping()
	@Operation(summary = "Get Prometheus Real Time Metric")
	public ResponseEntity<List<ResponseDTO.RealTimeDTO>> getPrometheusRealTimeMetric(
		@RequestBody RequestDTO requestDTO) {
		return new ResponseEntity<>(monitorService.getRealTimeMetric(requestDTO), HttpStatus.OK);
	}

	/**
	 * 과거 모니터링 조회 API
	 * @param requestDTO
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping("/history")
	@Operation(summary = "Get Prometheus History Metric")
	public ResponseEntity<List<ResponseDTO.HistoryDTO>> getPrometheusHistoryMetric(@RequestBody RequestDTO requestDTO) {
		return new ResponseEntity<>(monitorService.getHistoryMetric(requestDTO), HttpStatus.OK);
	}

	/**
	 * k8s event list 조회 API
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping("/event")
	@Operation(summary = "Get Event List")
	public ResponseEntity<List<ResponseDTO.EventDTO>> getEventList(
		@RequestParam(name = "namespace", required = false) String namespace,
		@RequestParam(name = "podName", required = false) String podName) {
		return new ResponseEntity<>(monitorService.getEventList(namespace, podName), HttpStatus.OK);
	}

	/**
	 * 등록된 Promql List 조회하는 API
	 * @return 등록된 Promql List
	 */
	@GetMapping("/promql")
	@Operation(summary = "Get Promql List")
	public ResponseEntity<List<ResponseDTO.PromqlDTO>> getPromqlList() {
		return new ResponseEntity<>(monitorService.getPromqlList(), HttpStatus.OK);
	}

	/**
	 * 워크스페이스별 자원의 사용량 조회하는 API
	 * @return 조회된 사용량
	 */
	@GetMapping("/dashboard/workspace")
	@Operation(summary = "관리자 대시보드  워크스페이스 자원 사용량 리스트")
	public ResponseEntity<List<ResponseDTO.WorkspaceDTO>> getDashboardWorkspaceResourceList(){
		return new ResponseEntity<>(monitorService.getWorkspaceResourceList(), HttpStatus.OK);
	}
	/**
	 * 대시보드 노드 사용량 조회하는 API
	 */
	@GetMapping("/dashboard/node/{nodeName}")
	@Operation(summary = "관리자 대시보드  노드별 자원 사용량 조회")
	public ResponseEntity<ResponseDTO.NodeResourceDTO> getDashboardNodeResourceByNodeName(@PathVariable(name = "nodeName") String nodeName){
		return new ResponseEntity<>(monitorService.getNodeResource(nodeName), HttpStatus.OK);
	}
	/**
	 * 대시보드 노드 List 조회 API
	 * @return
	 */
	@GetMapping("/dashboard/node")
	@Operation(summary = "관리자 대시보드 Node 리스트 조회")
	public ResponseEntity<List<ResponseDTO.NodeResponseDTO>> getDashboardNodeList(
		@RequestParam(name = "status", required = false) String status){
		return new ResponseEntity<>(monitorService.getNodeList(status), HttpStatus.OK);
}

	/**
	 * 대시보드 워크로드 List 조회 API
	 * @return
	 */
	@GetMapping("/dashboard/wl")
	@Operation(summary = "관리자 대시보드 워크로드 리스트 조회")
	public ResponseEntity<List<ResponseDTO.WorkloadResponseDTO>> getDashboardWlList(
		@RequestParam(name = "status", required = false) String status){
		return new ResponseEntity<>(monitorService.getWlList(status), HttpStatus.OK);
	}

	/**
	 * 해당 WS의 Resource Info 조회 API
	 * @param namespace 조회될 WS name
	 * @return CPU,GPU,MEM등의 ResourceQuota, 상태별 워크로드 리스트
	 */
	@GetMapping("/ws/{namespace}")
	@Operation(summary = "Workspace Resource 조회")
	public ResponseEntity<ResponseDTO.WorkspaceResponseDTO> getWorkspaceResourcesInfo(@PathVariable(name = "namespace") String namespace){
		return new ResponseEntity<>(monitorService.getWorkspaceResourcesInfo(namespace), HttpStatus.OK);
	}
	@GetMapping("/dashboard/cluster")
	@Operation(summary = "관리자 대시보드 클러스터 조회")
	public ResponseEntity<List<ResponseDTO.ResponseClusterDTO>> getDashboardCluster(){
		return new ResponseEntity<>(monitorService.getDashboardCluster(), HttpStatus.OK);
	}
	@GetMapping("/cluster/resource")
	@Operation(summary = "클러스터 모니터링 리소스 Count 조회")
	public ResponseEntity<ResponseDTO.ClusterResourceDTO> getClusterResource(){
		return new ResponseEntity<>(monitorService.getClusterResource(), HttpStatus.OK);
	}

	@GetMapping("/cluster/objct")
	@Operation(summary = "클러스터 모니터링 Object 상태 현황 조화")
	public ResponseEntity<ResponseDTO.ClusterObjectDTO> getClusterObject(){
		return new ResponseEntity<>(monitorService.getClusterObject(), HttpStatus.OK);
	}

	@GetMapping("/cluster/reason")
	@Operation(summary = "클러스터 모니터링 K8s Warning Events by Reason")
	public ResponseEntity<Map<String, Map<String, Long>>> getClusterReason(
		@RequestParam(name = "minute", required = false) Long minute){
		return new ResponseEntity<>(monitorService.getClusterReason(minute), HttpStatus.OK);
	}
	@GetMapping("/cluster/pending-pod")
	@Operation(summary = "클러스터 모니터링 K8s pod Pending Count")
	public ResponseEntity<Map<String, Map<String, Long>>> getClusterPendingCount(
		@RequestParam(name = "minute", required = false) Long minute){
		return new ResponseEntity<>(monitorService.getClusterPendingCount(minute), HttpStatus.OK);
	}
	@GetMapping("/cluster/restart-container")
	@Operation(summary = "클러스터 모니터링 K8s Container Restart")
	public ResponseEntity<Map<String, Map<String, Long>>> getClusterContainerRestart(
		@RequestParam(name = "minute", required = false) Long minute){
		return new ResponseEntity<>(monitorService.getClusterContainerRestart(minute), HttpStatus.OK);
	}
	@GetMapping("/cluster/pending-fail-pod")
	@Operation(summary = "클러스터 모니터링 K8s pod pending and fail info")
	public ResponseEntity<List<ResponseDTO.ClusterPodInfo>> getClusterPendingAndFailPod(){
		return new ResponseEntity<>(monitorService.getClusterPendingAndFailPod(), HttpStatus.OK);
	}


}