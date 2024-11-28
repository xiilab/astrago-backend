package com.xiilab.modulek8s.node.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.node.dto.GpuInfoDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NodeServiceImpl implements NodeService{
	private final NodeRepository nodeRepository;

	@Override
	public GpuInfoDTO getGpuInfoByNodeName(String gpuName, String nodeName) {
		return nodeRepository.getGpuInfoByNodeName(gpuName, nodeName);
	}
}
