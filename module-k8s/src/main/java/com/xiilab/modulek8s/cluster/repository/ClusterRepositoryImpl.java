package com.xiilab.modulek8s.cluster.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClusterRepositoryImpl implements ClusterRepository{
	private final K8sAdapter k8sAdapter;

	public List<Namespace> getClusterList() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.namespaces().list().getItems();
		}
	}

	public Namespace getClusterByName(String clustName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.namespaces().withName(clustName).item();
		}
	}

	@Override
	public NodeList getNodeList() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.nodes().list();
		}
	}
}
