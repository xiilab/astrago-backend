package com.xiilab.modulek8s.workload.dto;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@SuperBuilder
public class JobResDTO extends WorkloadRes{
	public JobResDTO(Job job) {
		super(job);
		Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
		Map<String, Quantity> resourceRequests = container.getResources().getRequests();
		image = container.getImage();
		gpuRequest = resourceRequests.get("nvidia.com/gpu") != null ? Integer.parseInt(resourceRequests.get("nvidia.com/gpu").getAmount()) : 0;
		cpuRequest = resourceRequests.get("cpu") != null ? Integer.parseInt(resourceRequests.get("cpu").getAmount()) : 0;
		memRequest = resourceRequests.get("memory") != null ? Integer.parseInt(resourceRequests.get("memory").getAmount()) : 0;
		env = container.getEnv().stream().collect(Collectors.toMap(
			EnvVar::getName,
			EnvVar::getValue
			));
        workloadType = getWorkloadType();
		command = container.getCommand().get(2);
	}

    @Override
    public WorkloadRes convertResDTO(HasMetadata hasMetadata) {
        return null;
    }

    @Override
    public WorkloadType getWorkloadType() {
        return WorkloadType.BATCH;
    }

    @Override
    protected ResourceType getType() {
        return null;
    }
}