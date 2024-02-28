package com.xiilab.modulemonitor.config;

import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.OpenShiftClient;

@Component
public class MonitorK8sAdapter {
	/**
	 * ~/.kube/config 읽어온 Config
	 * @return KubernetesClient
	 */
	public KubernetesClient configServer() {
		Config config = new ConfigBuilder()
			.withRequestTimeout(0)
			.build();
		return new KubernetesClientBuilder().withConfig(config).build();
	}

	/**
	 * PrometheusRule 사용하기 위한 openshift-client
	 * @return
	 */
	public OpenShiftClient defaultOpenShiftClient(){
		return new KubernetesClientBuilder().build().adapt(OpenShiftClient.class);

		// return new DefaultOpenShiftClient();
	}
}
