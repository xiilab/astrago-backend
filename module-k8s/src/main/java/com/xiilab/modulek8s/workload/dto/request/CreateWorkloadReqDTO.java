package com.xiilab.modulek8s.workload.dto.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.dto.CodeDTO;
import com.xiilab.modulek8s.workload.dto.JobReqVO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateWorkloadReqDTO extends K8SResourceReqDTO {
	private String creatorName;
	private String workspace;
	private String creator;
	private String image;
	private List<CodeDTO> codes;
	private List<PortReqDTO> ports;
	private List<EnvReqDTO> envs;
	private String command;


	@Builder
	public CreateWorkloadReqDTO(String name, String description, String creatorName, String workspace, String creator, String image,
		List<CodeDTO> codes, List<PortReqDTO> ports, List<EnvReqDTO> envs, String command) {
        super(name, description, creatorName, creator);
        this.creatorName = creatorName;
		this.workspace = workspace;
		this.creator = creator;
		this.image = image;
		this.codes = codes;
		this.ports = ports;
		this.envs = envs;
		this.command = command;
	}

	public JobReqVO createWorkloadReqDtoToJobReqVO() {
		return JobReqVO.builder()
			.workspace(this.getWorkspace())
			.name(this.getName())
			.description(this.getDescription())
			.creatorName(this.getCreatorName())
			.creator(this.getCreator())
			.image(this.image)
			.codeReqs(this.getCodes())
			.port(portDtoListToMap(this.ports))
			.env(envDtoListToMap(this.envs))
			.command(this.command)
			.build();
	}

	private Map<String, Integer> portDtoListToMap(List<PortReqDTO> portReqDTOList) {
		return portReqDTOList.stream()
			.collect(Collectors.toMap(
				port -> port.getName(),
				port -> port.getPort()
			));
	}

	private Map<String, String> envDtoListToMap(List<EnvReqDTO> envReqDTOList) {
		return envReqDTOList.stream()
			.collect(Collectors.toMap(
				env -> env.getName(),
				env -> env.getValue()
			));
	}


}