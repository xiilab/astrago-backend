package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;

import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ModuleInteractiveJobResDTO extends ModuleWorkloadResDTO {
	public ModuleInteractiveJobResDTO(Deployment deployment) {
		super(deployment);

		Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
		name = getName();
		Map<String, Quantity> resourceRequests = container.getResources().getLimits();
		image = container.getImage();
		Quantity getGpuRequest = resourceRequests.get("nvidia.com/gpu");
		Quantity getCpuRequest = resourceRequests.get("cpu");
		Quantity getMemory = resourceRequests.get("memory");
		gpuRequest = getGpuRequest != null ? getGpuRequest.getAmount() + ResourcesUnit.GPU_UNIT.getUnit() :
			"0" + ResourcesUnit.GPU_UNIT.getUnit();
		cpuRequest = getCpuRequest != null ? getCpuRequest.getAmount() + ResourcesUnit.CPU_UNIT.getUnit() :
			"0" + ResourcesUnit.CPU_UNIT.getUnit();
		memRequest = getMemory != null ? getMemory.getAmount() + ResourcesUnit.MEM_UNIT.getUnit() :
			"0" + ResourcesUnit.MEM_UNIT.getUnit();
		envs = container.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		ports = container.getPorts().stream()
			.map(port -> new ModulePortResDTO(port.getName(), port.getContainerPort()))
			.toList();
		command = container.getCommand() != null ? null : container.getCommand().get(0);
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}
}
