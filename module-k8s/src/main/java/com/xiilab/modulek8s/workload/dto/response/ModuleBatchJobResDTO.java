package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;

import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ModuleBatchJobResDTO extends ModuleWorkloadResDTO {

	public ModuleBatchJobResDTO(Job job) {
		super(job);
		Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
		Map<String, Quantity> resourceRequests = container.getResources().getLimits();
		Quantity getGpuRequest = resourceRequests.get("nvidia.com/gpu");
		Quantity getCpuRequest = resourceRequests.get("cpu");
		Quantity getMemory = resourceRequests.get("memory");
		resourceName = getResourceName();
		image = container.getImage();
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
		command = container.getCommand().get(0);
		status = getWorkloadStatus(job.getStatus());
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}

	private WorkloadStatus getWorkloadStatus(JobStatus jobStatus) {
		Integer active = jobStatus.getActive();
		Integer failed = jobStatus.getFailed();
		Integer ready = jobStatus.getReady();
		if (failed != null && failed > 0) {
			return WorkloadStatus.ERROR;
		} else if (ready != null && ready > 0) {
			return WorkloadStatus.RUNNING;
		} else if (active != null && active > 0) {
			return WorkloadStatus.PENDING;
		} else {
			return WorkloadStatus.END;
		}
	}
}
