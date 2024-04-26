package com.xiilab.modulek8s.node.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;

import io.fabric8.kubernetes.api.model.Node;

public interface NodeRepository {
	ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize, String searchText);

	MIGProfileDTO getNodeMIGProfiles(String nodeName, int giCount);

	void updateMIGProfile(String nodeName, String option);

	MIGProfileDTO getNodeMIGProfileFromJson(String productName, int giCount);

	int getMIGProfileGICount(String productName, String profileName) throws IOException;

	Node getNode(String resourceName);

	ResponseDTO.NodeInfo getNodeByResourceName(String resourceName);

	ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName);

	void setSchedule(String resourceName, ScheduleType scheduleType);

	List<ResponseDTO.WorkerNodeDriverInfo> getWorkerNodeDriverInfos();

	void updateMigProfile(MIGGpuDTO MIGGpuDTO);

	MIGGpuDTO.MIGInfoStatus getNodeMigStatus(String nodeName);

	void updateNodeLabel(String nodeName, Map<String, String> labels);

	void saveGpuProductTOLabel(String nodeName);

	void restartMIGManager();
}
