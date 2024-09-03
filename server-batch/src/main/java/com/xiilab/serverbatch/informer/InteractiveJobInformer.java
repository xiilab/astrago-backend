package com.xiilab.serverbatch.informer;

import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.serverbatch.informer.handler.WorkloadHandler;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class InteractiveJobInformer {
	private final K8sAdapter k8sAdapter;
	private final WorkloadHandler workloadHandler;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	private void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Deployment> jobSharedIndexInformer = informers.sharedIndexInformerFor(
			Deployment.class, 30 * 60 * 1000L);
		jobSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {
			@Override
			public void onAdd(Deployment deployment) {
				workloadHandler.DeploymentAddHandler(deployment);
			}

			@Override
			public void onUpdate(Deployment deployment1, Deployment deployment2) {
				workloadHandler.deploymentUpdateHandler(deployment1, deployment2);
			}

			@Override
			public void onDelete(Deployment deployment, boolean b) {
				workloadHandler.deploymentDeleteHandler(deployment);
			}
		});

		log.info("Starting all registered interactive job informers");
		informers.startAllRegisteredInformers();
	}
}
