package com.xiilab.serverbatch.informer.handler;

import org.kubeflow.v2beta1.MPIJob;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;

public interface WorkloadHandler {
	void batchJobAddHandler(Job job);

	void batchJobUpdateHandler(Job beforeJob, Job afterJob);

	void batchJobDeleteHandler(Job job);

	void DeploymentAddHandler(Deployment deployment);

	void deploymentUpdateHandler(Deployment beforeDeployment, Deployment afterDeployment);

	void deploymentDeleteHandler(Deployment deployment);

	void distributedJobAddHandler(MPIJob mpiJob);

	void distributedJobUpdateHandler(MPIJob beforeJob, MPIJob afterJob);

	void distributedJobDeleteHandler(MPIJob mpiJob);
}
