package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleCreateWorkloadReqDTO extends CreateWorkloadReqDTO {
	public BatchJobVO toBatchJobVO(String workspaceName) {
		initializeCollection();

		return BatchJobVO.builder()
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
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.secretName(this.imageSecretName)
			.nodeName(this.nodeName)
			.gpuType(this.gpuType)
			.gpuName(this.gpuName)
			.gpuOnePerMemory(this.gpuOnePerMemory)
			.resourcePresetId(this.resourcePresetId)
			.userUUID(this.userUUID) // je.kim 한자연 전용 
			.build();
	}

	public InteractiveJobVO toInteractiveJobVO(String workspaceName) {
		initializeCollection();

		return InteractiveJobVO.builder()
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
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.secretName(this.imageSecretName)
			.ide(this.ide)
			.nodeName(this.nodeName)
			.gpuType(this.gpuType)
			.gpuName(this.gpuName)
			.gpuOnePerMemory(this.gpuOnePerMemory)
			.resourcePresetId(this.resourcePresetId)
			.expirationTime(this.expirationTime)
			.userUUID(this.userUUID)
			.build();
	}
}
