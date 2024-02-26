package com.xiilab.modulek8s.node.repository;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.node.dto.MigMixedDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.MIGStrategy;

import io.fabric8.kubernetes.api.model.Node;

public interface NodeRepository {
	List<ResponseDTO.NodeDTO> getNodeList();
	ResponseDTO.MIGProfile getNodeMIGProfiles(String nodeName);
	void updateMIGAllProfile(String nodeName, String option);
	int getGPUCount(Node node);
	MIGStrategy getNodeMIGOnOffYN(Map<String, String> nodeLabels);
	ResponseDTO.MIGProfile getNodeMIGProfileFromJson(String productName) throws FileNotFoundException;
	List<MigMixedDTO> getMigMixedInfo(Node node);

	Node getNode(String resourceName);

	ResponseDTO.NodeInfo getNodeByResourceName(String resourceName);
}
