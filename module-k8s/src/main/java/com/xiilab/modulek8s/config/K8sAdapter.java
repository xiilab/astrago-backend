package com.xiilab.modulek8s.config;

import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
@Component
public class K8sAdapter {
	/**
	 * ~/.kube/config 읽어온 Config
	 * @return
	 */
	public KubernetesClient configServer() {
		Config config = new ConfigBuilder()
			.build();
		return new KubernetesClientBuilder().withConfig(config).build();
	}
}
