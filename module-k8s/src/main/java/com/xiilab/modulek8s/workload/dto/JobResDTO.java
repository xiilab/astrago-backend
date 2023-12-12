package com.xiilab.modulek8s.workload.dto;

import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class JobResDTO extends WorkloadRes{
	public JobResDTO(Job job) {
		super(job);
		Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
		image = container.getImage();
		// gpuRequest = Integer.parseInt(container.getResources().getRequests().get("gpu").getAmount());
		// cpuRequest = Integer.parseInt(container.getResources().getRequests().get("cpu").getAmount());
		// memRequest = Integer.parseInt(container.getResources().getRequests().get("mem").getAmount());		gpuRequest = Integer.parseInt(container.getResources().getRequests().get("gpu").getAmount());
		gpuRequest = 0;
		cpuRequest = 0;
		memRequest = 0;
		env = container.getEnv().stream().collect(Collectors.toMap(
			EnvVar::getName,
			EnvVar::getValue
			));
		port = null;
		command = container.getCommand().get(2);
	}

	@Override
	public WorkloadRes convertResDTO(HasMetadata hasMetadata) {
		return null;
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}
}
