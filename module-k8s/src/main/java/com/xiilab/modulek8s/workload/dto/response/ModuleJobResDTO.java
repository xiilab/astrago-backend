package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;
import java.util.Objects;

import org.springframework.util.ObjectUtils;

import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleJobResDTO extends ModuleWorkloadResDTO {
	private WorkloadStatus status;
	private Map<Long, Map<String, String>> codesInfoMap;
	private Map<Long, Map<String, String>> datasetInfoMap;
	private Map<Long, Map<String, String>> modelInfoMap;

	public ModuleJobResDTO(Deployment deployment, Map<Long, Map<String, String>> codesInfoMap, Map<Long, Map<String, String>> datasetInfoMap, Map<Long, Map<String, String>> modelInfoMap) {
		super(deployment);
		initializeFromContainer(deployment.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = getWorkloadStatus(deployment.getStatus());
		this.codesInfoMap = codesInfoMap;
		this.datasetInfoMap = datasetInfoMap;
		this.modelInfoMap = modelInfoMap;
	}

	public ModuleJobResDTO(Job job, Map<Long, Map<String, String>> codesInfoMap, Map<Long, Map<String, String>> datasetInfoMap, Map<Long, Map<String, String>> modelInfoMap) {
		super(job);
		initializeFromContainer(job.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = getWorkloadStatus(job.getStatus());
		this.codesInfoMap = codesInfoMap;
		this.datasetInfoMap = datasetInfoMap;
		this.modelInfoMap = modelInfoMap;
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
		gpuRequest = getGpuRequest != null ? getGpuRequest.getAmount() : "0";
		cpuRequest = getCpuRequest != null ? getCpuRequest.getAmount() : "0";
		memRequest = getMemory != null ? getMemory.getAmount() : "0";
		envs = container.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		ports = container.getPorts().stream()
			.map(port -> new ModulePortResDTO(port.getName(), port.getContainerPort()))
			.toList();
		command = !ObjectUtils.isEmpty(container.getCommand())? container.getCommand().get(2) : null;
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
