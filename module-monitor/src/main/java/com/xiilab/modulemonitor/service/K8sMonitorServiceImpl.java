package com.xiilab.modulemonitor.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.repository.K8sMonitorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class K8sMonitorServiceImpl implements K8sMonitorService{
	private final K8sMonitorRepository k8sMonitorRepository;
	@Override
	public long getWorkloadErrorCount(String namespace) {
		return k8sMonitorRepository.getWorkloadErrorCount(namespace);
	}

	@Override
	public long getWorkloadCountByNamespace(String namespace) {
		return k8sMonitorRepository.getWorkloadCountByNamespace(namespace);
	}

	@Override
	public List<ResponseDTO.NodeResponseDTO> getNodeList() {
		return k8sMonitorRepository.getNodeList();
	}

	@Override
	public List<ResponseDTO.WorkloadResponseDTO> getWlList() {
		return k8sMonitorRepository.getWlList();
	}

	@Override
	public ResponseDTO.WorkspaceResponseDTO getWlList(String namespace) {
		return k8sMonitorRepository.getWlList(namespace);
	}

	@Override
	public List<ResponseDTO.EventDTO> getEventList() {
		return k8sMonitorRepository.getEventList();
	}

	@Override
	public List<ResponseDTO.EventDTO> getEventList(String namespace) {
		return k8sMonitorRepository.getEventList(namespace);
	}

	@Override
	public List<ResponseDTO.EventDTO> getEventList(String namespace, String podName) {
		return k8sMonitorRepository.getEventList(namespace, podName);
	}

	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterCPU(String nodeName, double cpuUsage) {
		return k8sMonitorRepository.getDashboardClusterCPU(nodeName, cpuUsage);
	}

	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterMEM(String nodeName, String memUsage) {
		return k8sMonitorRepository.getDashboardClusterMEM(nodeName, memUsage);
	}
	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterMemByNode(String nodeName, String memUsage) {
		return k8sMonitorRepository.getDashboardClusterMemByNode(nodeName, memUsage);
	}
	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterGPU(String nodeName) {
		return k8sMonitorRepository.getDashboardClusterGPU(nodeName);
	}
	@Override
	public Map<String, Map<String, Long>> getClusterReason(long minute){
		return k8sMonitorRepository.getClusterReason(minute);
	}
	@Override
	public String getNodeName(String podName, String namespace){
		return k8sMonitorRepository.getNodeName(podName, namespace);
	}
	@Override
	public ResponseDTO.ClusterPodInfo getClusterPendingAndFailPod(String podName, String namespace){
		return k8sMonitorRepository.getClusterPendingAndFailPod(podName, namespace);
	}
	@Override
	public String getWorkspaceName(String workspaceName){
		return k8sMonitorRepository.getWorkspaceName(workspaceName);
	}
}
