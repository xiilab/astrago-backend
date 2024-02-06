package com.xiilab.modulek8s.cluster.repository;

import io.fabric8.kubernetes.api.model.NodeList;

public interface ClusterRepository {
	NodeList getNodeList();
}
