package com.xiilab.servercore.workload.dto.request;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulek8s.common.dto.DistributedResourceDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateDistributedWorkloadReqDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateDistributedWorkloadJobReqDTO extends CreateWorkloadJobReqDTO {
	private DistributedResourceDTO.LauncherInfo launcherInfo;
	private DistributedResourceDTO.WorkerInfo workerInfo;

	@Override
	public CreateWorkloadReqDTO toModuleDTO(String initContainerUrl) {
		return ModuleCreateDistributedWorkloadReqDTO.builder()
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
			.launcherCpuRequest(launcherInfo.getCpuRequest())
			.launcherMemRequest(launcherInfo.getMemRequest())
			.workerCpuRequest(workerInfo.getCpuRequest())
			.workerMemRequest(workerInfo.getMemRequest())
			.workerGpuRequest(workerInfo.getGpuRequest())
			.workerCnt(workerInfo.getWorkerCnt())
			.creatorId(creatorId)
			.creatorUserName(creatorUserName)
			.creatorFullName(creatorFullName)
			.ide(ide.name())
			.initContainerUrl(initContainerUrl)
			.nodeName(nodeName)
			.gpuType(gpuType != null ? gpuType : GPUType.NORMAL)
			.gpuName(gpuName)
			.gpuOnePerMemory(gpuOnePerMemory)
			.resourcePresetId(resourcePresetId)
			.build();
	}

	@Override
	public float getTotalCpuRequest() {
		return this.launcherInfo.getCpuRequest() + (this.workerInfo.getCpuRequest() * this.workerInfo.getWorkerCnt());
	}

	@Override
	public float getTotalMemoryRequest() {
		return this.launcherInfo.getMemRequest() + (this.workerInfo.getMemRequest() * this.workerInfo.getWorkerCnt());
	}

	@Override
	public int getTotalGpuRequest() {
		return this.workerInfo.getGpuRequest() * this.workerInfo.getWorkerCnt();
	}
}
