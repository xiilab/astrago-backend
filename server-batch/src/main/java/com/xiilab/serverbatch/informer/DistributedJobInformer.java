package com.xiilab.serverbatch.informer;

import org.kubeflow.v2beta1.MPIJob;
import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.serverbatch.informer.handler.WorkloadHandler;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedJobInformer {
	private final K8sAdapter k8sAdapter;
	private final WorkloadHandler workloadHandler;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	public void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		MixedOperation<MPIJob, KubernetesResourceList<MPIJob>, Resource<MPIJob>> mpiJobClient = kubernetesClient.resources(
			MPIJob.class);
		SharedIndexInformer<MPIJob> informers = mpiJobClient.inAnyNamespace().inform();
		informers.addEventHandler(new ResourceEventHandler<>() {

			@Override
			public void onAdd(MPIJob mpiJob) {
				workloadHandler.distributedJobAddHandler(mpiJob);
			}

			@Override
			public void onUpdate(MPIJob mpiJob1, MPIJob mpijob2) {
				workloadHandler.distributedJobUpdateHandler(mpiJob1, mpijob2);
			}

			@Override
			public void onDelete(MPIJob mpiJob, boolean b) {
				workloadHandler.distributedJobDeleteHandler(mpiJob);
			}

		});

		log.info("Starting all registered distributed job informers");
		informers.start();
	}
}
