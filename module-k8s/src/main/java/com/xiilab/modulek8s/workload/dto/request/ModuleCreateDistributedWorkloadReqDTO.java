package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.vo.DistributedJobVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleCreateDistributedWorkloadReqDTO extends CreateWorkloadReqDTO {
	private float launcherCpuRequest;
	private float launcherMemRequest;
	private float workerCpuRequest;
	private float workerMemRequest;
	private Integer workerGpuRequest;
	private int workerCnt;

	public DistributedJobVO toDistributedJobVO(String workspaceName) {
		initializeCollection();

		return DistributedJobVO.builder()
			.workspace(this.workspace)
			.workspaceName(workspaceName)
			.name(this.getName())
			.description(this.getDescription())
			.creatorId(this.getCreatorId())
			.creatorUserName(this.getCreatorUserName())
			.creatorFullName(this.getCreatorFullName())
			.image(this.image.toJobImageVO(this.workspace))
			.codes(this.codes.stream().map(codeDTO -> codeDTO.toJobCodeVO(workspace, initContainerUrl)).toList())
			.datasets(this.datasets.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.models(this.models.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.workingDir(this.workingDir)
			.command(this.command)
			.parameter(this.parameter)
			.workloadType(this.workloadType)
			.launcherCpuRequest(this.launcherCpuRequest)
			.launcherMemRequest(this.launcherMemRequest)
			.workerCpuRequest(this.workerCpuRequest)
			.workerMemRequest(this.workerMemRequest)
			.workerGpuRequest(this.workerGpuRequest)
			.workerCnt(this.workerCnt)
			.secretName(this.imageSecretName)
			.nodeName(this.nodeName)
			.gpuType(this.gpuType)
			.gpuName(this.gpuName)
			.gpuOnePerMemory(this.gpuOnePerMemory)
			.resourcePresetId(this.resourcePresetId)
			.build();
	}
}
