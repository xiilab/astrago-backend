package com.xiilab.modulek8s.workload.dto.response.abst;

import java.util.Map;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractSingleWorkloadResDTO extends AbstractModuleWorkloadResDTO {
	protected float cpuRequest;                   // 워크로드 cpu 요청량
	protected float memRequest;                   // 워크로드 mem 요청량
	protected int gpuRequest;

	protected AbstractSingleWorkloadResDTO(HasMetadata hasMetadata) {
		super(hasMetadata);
	}

	protected void initializeResources(Map<String, Quantity> resourceRequests) {
		Quantity gpu = super.getGpuType() != GPUType.MPS? resourceRequests.get("nvidia.com/gpu") : resourceRequests.get("nvidia.com/gpu.shared");
		Quantity cpu = resourceRequests.get("cpu");
		Quantity memory = resourceRequests.get("memory");
		this.gpuRequest = gpu != null ? Integer.parseInt(gpu.getAmount()) : 0;
		this.cpuRequest = cpu != null ? K8sInfoPicker.convertQuantity(cpu) : 0;
		this.memRequest = memory != null ? (K8sInfoPicker.convertQuantity(memory)) : 0;
	}
}
