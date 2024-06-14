package com.xiilab.servercore.workload.dto.request;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSingleWorkloadJobReqDTO extends CreateWorkloadJobReqDTO {
	private float cpuRequest;
	private float memRequest;
	private int gpuRequest;

	@Override
	public ModuleCreateWorkloadReqDTO toModuleDTO(String initContainerUrl) {
		return ModuleCreateWorkloadReqDTO.builder()
			.name(getName())
			.description(getDescription())
			.workspace(workspace)
			.workloadType(workloadType)
			.image(image)
			.datasets(datasets)
			.models(models)
			.ports(ports)
			.envs(envs)
			.codes(codes)
			.workingDir(workingDir)
			.command(command)
			.parameter(parameter)
			.cpuRequest(cpuRequest)
			.gpuRequest(gpuRequest)
			.memRequest(memRequest)
			.creatorId(creatorId)
			.creatorUserName(creatorUserName)
			.creatorFullName(creatorFullName)
			.ide(ide.name())
			.initContainerUrl(initContainerUrl)
			.nodeName(nodeName)
			.gpuType(gpuType != null? gpuType : GPUType.NORMAL)
			.gpuName(gpuName)
			.build();
	}

	@Override
	public float getTotalCpuRequest() {
		return cpuRequest;
	}

	@Override
	public float getTotalMemoryRequest() {
		return memRequest;
	}

	@Override
	public int getTotalGpuRequest() {
		return gpuRequest;
	}
}
