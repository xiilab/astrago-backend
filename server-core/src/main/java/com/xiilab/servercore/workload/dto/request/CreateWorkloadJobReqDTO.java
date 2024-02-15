package com.xiilab.servercore.workload.dto.request;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkloadJobReqDTO extends APIBaseReqDTO {
	private String workspace;    // 워크스페이스명
	private WorkloadType workloadType;
	private Long credentialId;
	private ModuleImageReqDTO image;
	private List<ModulePortReqDTO> ports;
	private List<ModuleEnvReqDTO> envs;
	private List<ModuleVolumeReqDTO> datasets;
	private List<ModuleVolumeReqDTO> models;
	private List<ModuleCodeReqDTO> codes;
	private String command;
	private int cpuRequest;
	private int gpuRequest;
	private int memRequest;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;
	// SchedulingType schedulingType;        // 스케줄링 방식


	public ModuleCreateWorkloadReqDTO toModuleDTO() {
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
			.command(command)
			.cpuRequest(cpuRequest)
			.gpuRequest(gpuRequest)
			.memRequest(memRequest)
			.creatorId(creatorId)
			.creatorUserName(creatorUserName)
			.creatorFullName(creatorFullName)
			.build();

	}

	public void setUserInfo(String creatorId, String creatorName, String creatorFullName) {
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}

}
