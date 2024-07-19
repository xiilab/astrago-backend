package com.xiilab.servermonitor.monitor.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xiilab.modulemonitor.dto.ClusterObjectDTO;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.ClusterObject;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.K8sMonitorService;
import com.xiilab.modulemonitor.service.MonitorFacadeService;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorService {
	private final PrometheusService prometheus;
	private final K8sMonitorService k8sMonitorService;
	private final MonitorFacadeService monitorFacadeService;

	/**
	 * Prometheus 실시간 데이터 조회하는 메소드
	 * @param requestDTO pod, node, namespace 정보가 담긴 객체
	 * @return Prometheus에서 조회된 실시간 데이터 리스트
	 */
	public List<ResponseDTO.RealTimeDTO> getRealTimeMetric(RequestDTO requestDTO) {
		return prometheus.getRealTimeMetric(requestDTO);

	}

	/**
	 * 과거 Promethrus metric을 조회하는 메소드
	 * @param requestDTO 조회될 metric 정보가 담긴 객체
	 * @return 조회된 ResponseDTO	 List
	 */
	public List<ResponseDTO.HistoryDTO> getHistoryMetric(RequestDTO requestDTO) {
		return prometheus.getHistoryMetric(requestDTO);
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
		return monitorFacadeService.getWorkspaceResourceList();
	}

	public ResponseDTO.NodeResourceDTO getNodeResource(String nodeName){
		return monitorFacadeService.getNodeResource(nodeName);
	}

	/**
	 * 대시보드 노드 리스트 출력 메소드
	 * @return
	 */
	public List<ResponseDTO.NodeResponseDTO> getNodeList(String status){
		List<ResponseDTO.NodeResponseDTO> nodeList = k8sMonitorService.getNodeList();
		if(StringUtils.hasText(status)){
			return nodeList.stream().filter(nodeResponseDTO ->
				nodeResponseDTO.status().equals(status)).toList();
		}else{
			return nodeList;
		}
	}

	/**
	 * 대시보드 워크로드 리스트 출력 메소드
	 * @return
	 */
	public List<ResponseDTO.WorkloadResponseDTO> getWlList(String status){
		List<ResponseDTO.WorkloadResponseDTO> wlList = k8sMonitorService.getWlList();
		if(StringUtils.hasText(status)){
			return wlList.stream().filter(workloadResponseDTO ->
				workloadResponseDTO.status().equals(status)).toList();
		}else {
			return wlList;
		}
	}

	/**
	 * 해당 WS의 Resource Info 조회 메소드
	 * @param namespace 조회될 WS name
	 * @return CPU,GPU,MEM등의 ResourceQuota, 상태별 워크로드 리스트
	 */
	public ResponseDTO.WorkspaceResponseDTO getWorkspaceResourcesInfo(String namespace){
		return k8sMonitorService.getWlList(namespace);
	}

	public List<ResponseDTO.ResponseClusterDTO> getDashboardCluster(){
		return monitorFacadeService.getDashboardCluster();
	}
	public ResponseDTO.ClusterResourceDTO getClusterResource(){
		return monitorFacadeService.getClusterResource();
	}
	public ResponseDTO.ClusterObjectDTO getClusterObject(){
		return monitorFacadeService.getClusterObject();
	}
	public Map<String, Map<String, Long>> getClusterReason(long minute){
		return k8sMonitorService.getClusterReason(minute);
	}
	public Map<String, Map<String, Long>> getClusterPendingCount(long minute){
		return monitorFacadeService.getClusterPendingCount(minute);
	}
	public Map<String, Map<String, Long>> getClusterContainerRestart(long minute){
		return monitorFacadeService.getClusterContainerRestart(minute);
	}
	public List<ResponseDTO.ClusterPodInfo> getClusterPendingAndFailPod(){
		return monitorFacadeService.getClusterPendingAndFailPod();
	}
	public List<ClusterObjectDTO> getClusterObjectByObject(ClusterObject clusterObject){
		return monitorFacadeService.getClusterObjectByObject(clusterObject);
	}

	public List<ResponseDTO.HistoryDTO> getMultiCPUUtilization(RequestDTO requestDTO) {
		return monitorFacadeService.getMultiCPUUtilization(requestDTO);
	}
}

