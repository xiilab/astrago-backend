package com.xiilab.servercore.node.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8s.node.repository.NodeRepository;

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
}
