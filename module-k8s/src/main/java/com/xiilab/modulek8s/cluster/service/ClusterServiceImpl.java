package com.xiilab.modulek8s.cluster.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.cluster.repository.ClusterRepository;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {
	private final ClusterRepository clusterRepository;

	@Override
	public ClusterResourceDTO getClusterResource() {
		NodeList nodeList = clusterRepository.getNodeList();
		List<Node> nodeItems =  nodeList.getItems();
		for (Node nodeItem : nodeItems) {
			Map<String, String> labels = nodeItem.getMetadata().getLabels();
			if (Objects.nonNull(labels) && labels.containsKey("nvidia.com/mig.config.state")) {
				if (labels.get("nvidia.com/mig.config.state").equals("success")) {

				}
			}
		}
		return K8sInfoPicker.getClusterResource(nodeList);
	}
}




