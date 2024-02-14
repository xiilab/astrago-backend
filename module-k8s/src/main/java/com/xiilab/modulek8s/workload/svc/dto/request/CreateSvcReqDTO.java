package com.xiilab.modulek8s.workload.svc.dto.request;

import java.util.List;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.svc.enums.SvcType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateSvcReqDTO extends K8SResourceReqDTO {
	private String workspace;
	private SvcType svcType;
	private String jobName;
	private List<CreateSvcPortReqDTO> ports;

	public static CreateSvcReqDTO createWorkloadReqDTOToCreateServiceDto(
		ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO, String jobName) {
		return CreateSvcReqDTO.builder()
			.name(moduleCreateWorkloadReqDTO.getName())
			.description(moduleCreateWorkloadReqDTO.getDescription())
			.creatorId(moduleCreateWorkloadReqDTO.getCreatorId())
			.creatorUserName(moduleCreateWorkloadReqDTO.getCreatorUserName())
			.workspace(moduleCreateWorkloadReqDTO.getWorkspace())
			.svcType(SvcType.NODE_PORT)
			.jobName(jobName)
			.ports(moduleCreateWorkloadReqDTO.getPorts().stream().map(port -> new CreateSvcPortReqDTO(port.name(), port.port())).toList())
			.build();
	}
}
