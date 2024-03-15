package com.xiilab.modulek8s.node.repository;

import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.MigMixedDTO;
import com.xiilab.modulek8s.node.dto.NodeGpuDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.MIGStrategy;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;

import io.fabric8.kubernetes.api.model.Node;

public interface NodeRepository {
	ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize);

	MIGProfileDTO getNodeMIGProfiles(String nodeName, int giCount);

	void updateMIGProfile(String nodeName, String option);

	int getGPUCount(Node node);

	MIGStrategy getNodeMIGOnOffYN(Map<String, String> nodeLabels);

	MIGProfileDTO getNodeMIGProfileFromJson(String productName, int giCount);

	List<MigMixedDTO> getMigMixedInfo(Node node);

	Node getNode(String resourceName);

	ResponseDTO.NodeInfo getNodeByResourceName(String resourceName);

	ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName);

	void setSchedule(String resourceName, ScheduleType scheduleType);
	List<ResponseDTO.WorkerNodeDriverInfo> getWorkerNodeDriverInfos();
	void updateMigProfile(NodeGpuDTO nodeGpuDTO);
}
