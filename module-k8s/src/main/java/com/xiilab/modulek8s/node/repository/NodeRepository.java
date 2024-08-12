package com.xiilab.modulek8s.node.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulek8s.node.dto.GpuInfoDTO;
import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.MPSGpuDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;

import io.fabric8.kubernetes.api.model.Node;

public interface NodeRepository {
	List<Node> getGpuNodes(boolean isWorker);
	ResponseDTO.NodeGPUs getNodeGPUs(NodeType nodeType);
	ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize, String searchText);

	MIGProfileDTO getNodeMIGProfiles(String nodeName, int giCount);

	void updateMIGProfile(String nodeName, String option);

	MIGProfileDTO getNodeMIGProfileFromJson(String productName, int giCount);

	int getMIGProfileGICount(String productName, String profileName) throws IOException;

	void syncMigConfigMap(MIGGpuDTO migGpuDTO);

	Node getNode(String resourceName);

	ResponseDTO.NodeInfo getNodeByResourceName(String resourceName);

	ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName);

	void setSchedule(String resourceName, ScheduleType scheduleType);

	List<ResponseDTO.WorkerNodeDriverInfo> getWorkerNodeDriverInfos();

	void updateMigProfile(MIGGpuDTO MIGGpuDTO);

	MIGGpuDTO.MIGInfoStatus getNodeMigStatus(String nodeName);

	Map<String, Object> getMigConfigMap();

	void updateNodeLabel(String nodeName, Map<String, String> labels);

	void saveGpuProductTOLabel(String nodeName);

	void restartMIGManager();

	MPSGpuDTO.MPSInfoDTO getMpsConfig(String nodeName);

	void setMpsConfig(MPSGpuDTO.SetMPSDTO setMPSDTO);

	GpuInfoDTO getGpuInfoByNodeName(String gpuName, String nodeName);

	// ResponseDTO.PageNodeDTO getNodesByGpuName(int pageNo, int pageSize, String gpuName);
}
