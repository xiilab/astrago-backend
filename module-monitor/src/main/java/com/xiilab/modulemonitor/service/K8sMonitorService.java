package com.xiilab.modulemonitor.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.dto.ClusterObjectDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

@Service
public interface K8sMonitorService {
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

	ResponseDTO.ResponseClusterDTO getDashboardClusterMemByNode(String nodeName, String memUsage);

	ResponseDTO.ResponseClusterDTO getDashboardClusterGPU(String nodeName);

	ResponseDTO.ResponseClusterDTO getDashboardClusterMIG(String nodeName);

	Map<String, Map<String, Long>> getClusterReason(long minute);

	String getNodeName(String podName, String namespace);

	ResponseDTO.ClusterPodInfo getClusterPendingAndFailPod(String podName, String namespace);

	String getWorkspaceName(String workspaceName);

	List<ClusterObjectDTO> getClusterRunningPods();

	List<ClusterObjectDTO> getClusterPendingPods();

	List<ClusterObjectDTO> getClusterFailPods();

	List<ClusterObjectDTO> getReadyNodes();

	List<ClusterObjectDTO> getUnhealthyDeployments();

	List<ClusterObjectDTO> getUnhealthyHpas();

	List<ClusterObjectDTO> getUnhealthyDaemonSets();

	List<ClusterObjectDTO> getUnhealthyStatefulSets();

	List<ClusterObjectDTO> getContainerRestart();

	List<ClusterObjectDTO> getContainerImageRestart();

	Long getCpuCore(String nodeName);
}
