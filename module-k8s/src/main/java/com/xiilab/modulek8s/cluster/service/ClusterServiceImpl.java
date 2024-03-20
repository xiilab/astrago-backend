package com.xiilab.modulek8s.cluster.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.cluster.repository.ClusterRepository;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.NodeList;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {
	private final ClusterRepository clusterRepository;

	@Override
	public ClusterResourceDTO getClusterResource() {
		NodeList nodeList = clusterRepository.getNodeList();
		return K8sInfoPicker.getClusterResource(nodeList);
	}
}




