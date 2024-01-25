package com.xiilab.modulemonitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
}
