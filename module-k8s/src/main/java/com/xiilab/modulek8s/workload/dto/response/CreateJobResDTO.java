package com.xiilab.modulek8s.workload.dto.response;

import java.util.Map;

import org.kubeflow.v2beta1.MPIJob;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateJobResDTO extends AbstractModuleWorkloadResDTO {
	private WorkloadStatus status;
	private String gpuRequest;                   // 워크로드 gpu 요청량
	private String cpuRequest;                   // 워크로드 cpu 요청량
	private String memRequest;                   // 워크로드 mem 요청량
	private Map<Long, Map<String, String>> codesInfoMap;
	private Map<Long, Map<String, String>> datasetInfoMap;
	private Map<Long, Map<String, String>> modelInfoMap;

	public CreateJobResDTO(Deployment deployment, Map<Long, Map<String, String>> codesInfoMap, Map<Long, Map<String, String>> datasetInfoMap, Map<Long, Map<String, String>> modelInfoMap) {
		super(deployment);
		initializeFromContainer(deployment.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = K8sInfoPicker.getInteractiveWorkloadStatus(deployment.getStatus());
		this.codesInfoMap = codesInfoMap;
		this.datasetInfoMap = datasetInfoMap;
		this.modelInfoMap = modelInfoMap;
	}

	public CreateJobResDTO(Job job, Map<Long, Map<String, String>> codesInfoMap, Map<Long, Map<String, String>> datasetInfoMap, Map<Long, Map<String, String>> modelInfoMap) {
		super(job);
		initializeFromContainer(job.getSpec().getTemplate().getSpec().getContainers().get(0));
		status = K8sInfoPicker.getBatchWorkloadStatus(job.getStatus());
		this.codesInfoMap = codesInfoMap;
		this.datasetInfoMap = datasetInfoMap;
		this.modelInfoMap = modelInfoMap;
	}

	public CreateJobResDTO(MPIJob resource, Map<Long, Map<String, String>> codesInfoMap,
		Map<Long, Map<String, String>> datasetInfoMap, Map<Long, Map<String, String>> modelInfoMap) {
		super(resource);
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
			.map(port -> ModulePortResDTO.builder().name(port.getName()).originPort(port.getContainerPort()).build())
			.toList();
		command = !ObjectUtils.isEmpty(container.getCommand())? container.getCommand().get(2) : null;
	}

}
