package com.xiilab.modulek8s.service.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;
import com.xiilab.modulek8s.service.enums.ServiceType;
import com.xiilab.modulek8s.service.vo.ServiceVO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateServiceDTO extends K8SResourceReqDTO {
	private String workspace;
	private ServiceType serviceType;
	private String jobName;
	private List<Integer> ports;

	public CreateServiceDTO(String name, String description, String creatorName, String creator, String workspace,
		ServiceType serviceType, String jobName, List<Integer> ports) {
		super(name, description, creatorName, creator);
		this.workspace = workspace;
		this.serviceType = serviceType;
		this.jobName = jobName;
		this.ports = ports;
	}

	public static CreateServiceDTO CreateWorkloadReqDTOToCreateServiceDto(CreateWorkloadReqDTO createWorkloadReqDTO, String jobName) {
		return CreateServiceDTO.builder()
			.name(createWorkloadReqDTO.getName())
			.description(createWorkloadReqDTO.getDescription())
			.creatorName(createWorkloadReqDTO.getCreatorName())
			.workspace(createWorkloadReqDTO.getWorkspace())
			.serviceType(ServiceType.NODE_PORT)
			.jobName(jobName)
			.ports(createWorkloadReqDTO.getPorts().stream().map(port -> port.port()).collect(Collectors.toList()))
			.build();
	}
}
