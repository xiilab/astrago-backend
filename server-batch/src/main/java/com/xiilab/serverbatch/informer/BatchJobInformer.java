package com.xiilab.serverbatch.informer;

import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.serverbatch.informer.handler.WorkloadHandler;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobInformer {
	private final K8sAdapter k8sAdapter;
	private final WorkloadHandler workloadHandler;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	public void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Job> jobSharedIndexInformer = informers.sharedIndexInformerFor(
			Job.class, 30 * 60 * 1000L);
		jobSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {

			@Override
			public void onAdd(Job job) {
				workloadHandler.batchJobAddHandler(job);
			}

			@Override
			public void onUpdate(Job job1, Job job2) {
				workloadHandler.batchJobUpdateHandler(job1, job2);
			}

			@Override
			public void onDelete(Job job, boolean b) {
				workloadHandler.batchJobDeleteHandler(job);
			}
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}
}
