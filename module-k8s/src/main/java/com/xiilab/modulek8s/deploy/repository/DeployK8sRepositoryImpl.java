package com.xiilab.modulek8s.deploy.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeployK8sRepositoryImpl implements DeployK8sRepository{
	private final K8sAdapter k8sAdapter;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<Pod> getReplicasByDeployResourceName(String workspaceResourceName, String deployResourceName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			return client.pods()
				.inNamespace(workspaceResourceName)
				.withLabel("app", deployResourceName)
				.list().getItems();
		}
	}
}
