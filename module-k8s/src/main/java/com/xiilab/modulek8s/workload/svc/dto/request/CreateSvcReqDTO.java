package com.xiilab.modulek8s.workload.svc.dto.request;

import java.util.List;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
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

	public CreateSvcReqDTO(String name, String description, String creatorName, String creator, String workspace,
		SvcType svcType, String jobName, List<CreateSvcPortReqDTO> ports) {
		super(name, description, creatorName, creator);
		this.workspace = workspace;
		this.svcType = svcType;
		this.jobName = jobName;
		this.ports = ports;
	}

	public static CreateSvcReqDTO createWorkloadReqDTOToCreateServiceDto(CreateWorkloadReqDTO createWorkloadReqDTO, String jobName) {
		return CreateSvcReqDTO.builder()
			.name(createWorkloadReqDTO.getName())
			.description(createWorkloadReqDTO.getDescription())
			.creatorName(createWorkloadReqDTO.getCreatorName())
			.workspace(createWorkloadReqDTO.getWorkspace())
			.svcType(SvcType.NODE_PORT)
			.jobName(jobName)
			.ports(createWorkloadReqDTO.getPorts().stream().map(port -> new CreateSvcPortReqDTO(port.name(), port.port())).toList())
			.build();
	}
}
