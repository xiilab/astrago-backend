package com.xiilab.servercore.workload.dto.request;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkloadJobReqDTO extends APIBaseReqDTO {
	private String workspace;    // 워크스페이스명
	private WorkloadType workloadType;	//
	private ModuleImageReqDTO image;
	private List<ModulePortReqDTO> ports;
	private List<ModuleEnvReqDTO> envs;
	private List<ModuleVolumeReqDTO> volumes;
	private List<ModuleCodeReqDTO> codes;
	private String command;
	private int cpuRequest;
	private int gpuRequest;
	private int memRequest;
	private String creatorId;
	private String creatorName;
	// SchedulingType schedulingType;        // 스케줄링 방식


	public ModuleCreateWorkloadReqDTO toModuleDTO() {
		return ModuleCreateWorkloadReqDTO.builder()
			.name(getName())
			.description(getDescription())
			.workspace(workspace)
			.workloadType(workloadType)
			.image(image)
			.ports(ports)
			.envs(envs)
			.volumes(volumes)
			.codes(codes)
			.command(command)
			.cpuRequest(cpuRequest)
			.gpuRequest(gpuRequest)
			.memRequest(memRequest)
			.creatorId(creatorId)
			.creatorName(creatorName)
			.build();

	}

	public void setUserInfo(String creatorId, String creatorName) {
		this.creatorId = creatorId;
		this.creatorName = creatorName;
	}

}
