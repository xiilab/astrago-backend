package com.xiilab.servercore.deploy.dto;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class CreateDeployReqDTO extends APIBaseReqDTO {
	private String workspace;    // 워크스페이스명
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
	private int modelId;
	private String modelSaveName;
	private List<String> modelConfigNames;
	private int replica;
	//nvidia triton config file
	private String tritonConfigText;
	//확장자 검사해야함
	private MultipartFile tritonConfigFile;


	public void setUserInfo(String creatorId, String creatorName, String creatorFullName) {
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}
	public void setNodeName(String nodeName){
		this.nodeName = nodeName;
	}

	public ModuleCreateWorkloadReqDTO toModuleDTO(String initContainerURL) {
		if(deployType == DeployType.TRITON){
			return ModuleCreateWorkloadReqDTO.builder()
				.name(getName())
				.description(getDescription())
				.workspace(workspace)
				.workloadType(workloadType)
				.image(image)
				.ports(ports)
				.envs(envs)
				.workingDir(workingDir)
				.command(command)
				.cpuRequest(cpuRequest)
				.gpuRequest(gpuRequest)
				.memRequest(memRequest)
				.creatorId(creatorId)
				.creatorUserName(creatorUserName)
				.creatorFullName(creatorFullName)
				.initContainerUrl(initContainerURL)
				.nodeName(nodeName)
				.gpuType(gpuType != null? gpuType : GPUType.NORMAL)
				.gpuName(gpuName)
				.gpuOnePerMemory(gpuOnePerMemory)
				.resourcePresetId(resourcePresetId)
				.build();
		}else{
			return null;
		}
	}
}
