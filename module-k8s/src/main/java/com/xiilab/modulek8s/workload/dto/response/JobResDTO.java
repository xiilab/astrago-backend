package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;
import java.util.stream.Collectors;

import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class JobResDTO extends WorkloadResDTO {
	public JobResDTO(Job job) {
		super(job);

		Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
		name = getName();
		Map<String, Quantity> resourceRequests = container.getResources().getLimits();
		image = container.getImage();
		Quantity getGpuRequest = resourceRequests.get("nvidia.com/gpu");
		Quantity getCpuRequest = resourceRequests.get("cpu");
		Quantity getMemory = resourceRequests.get("memory");
		gpuRequest = getGpuRequest != null ? getGpuRequest.getAmount() + ResourcesUnit.GPU_UNIT.getUnit() : "0" + ResourcesUnit.GPU_UNIT.getUnit();
		cpuRequest = getCpuRequest != null ? getCpuRequest.getAmount() + ResourcesUnit.CPU_UNIT.getUnit() : "0" + ResourcesUnit.CPU_UNIT.getUnit();
		memRequest = getMemory != null ? getMemory.getAmount() + ResourcesUnit.MEM_UNIT.getUnit() : "0" + ResourcesUnit.MEM_UNIT.getUnit();
		envs = container.getEnv().stream()
			.map(env -> new EnvResDTO(env.getName(), env.getValue()))
			.collect(Collectors.toList());
		ports = container.getPorts().stream()
			.map(port -> new PortResDTO(port.getName(), port.getContainerPort()))
			.collect(Collectors.toList());
		command = container.getCommand().get(0);
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}
}
