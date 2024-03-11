package com.xiilab.modulemonitor.repository;

import java.util.List;
import java.util.Map;

import com.xiilab.modulemonitor.dto.ResponseDTO;

public interface K8sMonitorRepository {
	long getWorkloadErrorCount(String namespace);
	long getWorkloadCountByNamespace(String namespace);
	List<ResponseDTO.NodeResponseDTO> getNodeList();
	List<ResponseDTO.WorkloadResponseDTO> getWlList();
	ResponseDTO.WorkspaceResponseDTO getWlList(String namespace);
	List<ResponseDTO.EventDTO> getEventList();
	List<ResponseDTO.EventDTO> getEventList(String namespace);
	List<ResponseDTO.EventDTO> getEventList(String namespace, String podName);
	ResponseDTO.ResponseClusterDTO getDashboardClusterCPU(String nodeName, double cpuUsage);
	ResponseDTO.ResponseClusterDTO getDashboardClusterMEM(String nodeName, String memUsage);
	ResponseDTO.ResponseClusterDTO getDashboardClusterGPU(String nodeName);
	Map<String, Map<String, Long>> getClusterReason(long minute);
	String getNodeName(String podName, String namespace);
	ResponseDTO.ClusterPodInfo getClusterPendingAndFailPod(String podName, String namespace);
	String getWorkspaceName(String workspaceName);
}
