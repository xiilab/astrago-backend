package com.xiilab.servercore.node.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NodeFacadeService {
	private final NodeRepository nodeRepository;

	public List<ResponseDTO.NodeDTO> getNodeList(){
		List<ResponseDTO.NodeDTO> nodeList = nodeRepository.getNodeList();
		return null;
	}

	/**
	 * nodeName로 가능한 mig profile list를 조회하는 메소드
	 *
	 * @param nodeName 노드의 Name
	 * @return
	 */
	public ResponseDTO.MIGProfile getNodeMIGProfiles(String nodeName){
		return nodeRepository.getNodeMIGProfiles(nodeName);
	}
	/**
	 * mig profile을 update 하는 메소드
	 *
	 * @param nodeName 노드 Name
	 * @param option mig 요청 profile
	 */
	public void updateMIGAllProfile(String nodeName, String option) {
		nodeRepository.updateMIGAllProfile(nodeName, option);
	}
}
