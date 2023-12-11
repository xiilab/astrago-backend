package com.xiilab.modulemonitor.cluster;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;

@Service
public class ClusterService {
	@Autowired
	private K8sAdapter k8sAdapter;

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

}
