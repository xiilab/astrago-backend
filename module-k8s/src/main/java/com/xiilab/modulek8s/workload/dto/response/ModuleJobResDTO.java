package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;
import java.util.Objects;

import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ModuleJobResDTO extends ModuleWorkloadResDTO{
	private WorkloadStatus status;

	public ModuleJobResDTO(Deployment deployment) {
		super(deployment);
		initializeFromContainer(deployment.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = getWorkloadStatus(deployment.getStatus());
	}

	public ModuleJobResDTO(Job job) {
		super(job);
		initializeFromContainer(job.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = getWorkloadStatus(job.getStatus());
	}

	@Override
	public WorkloadType getType() {
		return type;
	}

	private void initializeFromContainer(Container container) {
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

	private WorkloadStatus getWorkloadStatus(DeploymentStatus deploymentStatus) {
		Integer replicas = deploymentStatus.getReplicas();
		Integer availableReplicas = deploymentStatus.getAvailableReplicas();
		Integer unavailableReplicas = deploymentStatus.getUnavailableReplicas();
		if (unavailableReplicas != null && unavailableReplicas > 0) {
			return WorkloadStatus.ERROR;
		} else if (availableReplicas != null && Objects.equals(replicas, availableReplicas)) {
			return WorkloadStatus.RUNNING;
		} else {
			return WorkloadStatus.PENDING;
		}
	}

	private WorkloadStatus getWorkloadStatus(JobStatus jobStatus) {
		Integer active = jobStatus.getActive();
		Integer failed = jobStatus.getFailed();
		Integer succeeded = jobStatus.getSucceeded();
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
