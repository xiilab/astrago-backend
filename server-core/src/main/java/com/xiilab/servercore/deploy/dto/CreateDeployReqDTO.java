package com.xiilab.servercore.deploy.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.deploy.dto.request.ModuleCreateDeployReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class CreateDeployReqDTO extends APIBaseReqDTO {
	private String workspaceResourceName;    // 워크스페이스명
	private WorkloadType workloadType;
	private DeployType deployType;
	private ModuleImageReqDTO image;
	private List<ModulePortReqDTO> ports;
	private List<ModuleEnvReqDTO> envs;
	private String workingDir;
	private String command;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;
	private String nodeName;
	private GPUType gpuType;
	private String gpuName;
	private int gpuOnePerMemory;
	private int resourcePresetId;
	private float cpuRequest;
	private float memRequest;
	private int gpuRequest;
	private long modelId;
	private String modelVersion;
	private String modelSaveName;
	private List<String> modelConfigNames;
	protected String imageSecretName;
	private int replica;
	//nvidia triton config file
	private String tritonConfigText;

	public void setUserInfo(String creatorId, String creatorName, String creatorFullName) {
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}
	public void setNodeName(String nodeName){
		this.nodeName = nodeName;
	}
	public void setPorts(List<ModulePortReqDTO> ports){
		this.ports = ports;
	}
	public ModuleCreateDeployReqDTO toTritonModuleDTO(List<ModuleVolumeReqDTO> volumes) {
		if(deployType == DeployType.TRITON){
			ModuleImageReqDTO imageReqDTO = ModuleImageReqDTO.builder()
				.name("nvcr.io/nvidia/tritonserver:23.02-py3")
				.type(ImageType.BUILT)
				.repositoryAuthType(RepositoryAuthType.PUBLIC)
				.build();
			return ModuleCreateDeployReqDTO.builder()
				.name(getName())
				.description(getDescription())
				.workspace(workspaceResourceName)
				.workloadType(workloadType)
				.image(imageReqDTO)
				.ports(ports)
				.workingDir(workingDir)
				.command("tritonserver --model-repository=/models")
				// .command("while true; do echo hello; sleep 10;done")
				.cpuRequest(cpuRequest)
				.gpuRequest(gpuRequest)
				.memRequest(memRequest)
				.creatorId(creatorId)
				.creatorUserName(creatorUserName)
				.creatorFullName(creatorFullName)
				.nodeName(nodeName)
				.gpuType(gpuType != null? gpuType : GPUType.NORMAL)
				.gpuName(gpuName)
				.gpuOnePerMemory(gpuOnePerMemory)
				.resourcePresetId(resourcePresetId)
				.imageSecretName(imageSecretName)
				.replica(replica)
				.imageType(ImageType.BUILT)
				.deployType(deployType)
				.deployModelId(modelId)
				.modelVersion(modelVersion)
				.build();
		}else{
			return ModuleCreateDeployReqDTO.builder()
				.name(getName())
				.description(getDescription())
				.workspace(workspaceResourceName)
				.workloadType(workloadType)
				.image(image)
				.ports(ports)
				.workingDir(workingDir)
				.command(command)
				.cpuRequest(cpuRequest)
				.gpuRequest(gpuRequest)
				.memRequest(memRequest)
				.creatorId(creatorId)
				.creatorUserName(creatorUserName)
				.creatorFullName(creatorFullName)
				.nodeName(nodeName)
				.gpuType(gpuType != null? gpuType : GPUType.NORMAL)
				.gpuName(gpuName)
				.gpuOnePerMemory(gpuOnePerMemory)
				.resourcePresetId(resourcePresetId)
				.imageSecretName(imageSecretName)
				.replica(replica)
				.imageType(ImageType.BUILT)
				.volumes(volumes)
				.deployType(deployType)
				.deployModelId(modelId)
				.build();
		}
	}

}
