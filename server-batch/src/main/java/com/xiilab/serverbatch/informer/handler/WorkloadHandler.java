package com.xiilab.serverbatch.informer.handler;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;

public interface WorkloadHandler {
	void batchJobAddHandler(Job job);

	void batchJobUpdateHandler(Job beforeJob, Job afterJob);

	void batchJobDeleteHandler(Job job);

	void interactiveJobAddHandler(Deployment deployment);

	void interactiveJobUpdateHandler(Deployment beforeDeployment, Deployment afterDeployment);

	void interactiveJobDeleteHandler(Deployment deployment);
}