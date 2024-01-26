package com.xiilab.modulek8s.workload.log.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class LogRepositoryImpl implements LogRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public LogWatch watchLogByWorkload(String workspace, String workload) {
		KubernetesClient client = k8sAdapter.configServer();
		return client.pods()
			.inNamespace(workspace)
			.withName(workload)
			.withPrettyOutput()
			.watchLog();
	}
}
