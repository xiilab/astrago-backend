package com.xiilab.servercore.node.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;

import io.fabric8.kubernetes.api.model.Node;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NodeService {
	private final NodeRepository nodeRepository;

	/**
	 * mig profile을 update 하는 메소드
	 *
	 * @param nodeName 노드 Name
	 * @param option   mig 요청 profile
	 */
	public void updateMIGAllProfile(String nodeName, String option) {
		nodeRepository.updateMIGProfile(nodeName, option);
	}
	public ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize) {
		return nodeRepository.getNodeList(pageNo, pageSize, null);
	}

	public List<Node> getNodeListIsWorker(boolean isWorker) {
		return nodeRepository.getGpuNodes(isWorker);
	}
}
