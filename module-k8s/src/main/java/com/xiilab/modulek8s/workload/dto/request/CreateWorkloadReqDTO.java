package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.enums.ImageType;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.vo.JobVO;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
public class CreateWorkloadReqDTO extends K8SResourceReqDTO {
	private String workspace;    // 워크스페이스명
	private ImageType imageType;    // 이미지 타입(빌트인, Dockerhub)
	private String image;    // 이미지명
	private List<CodeReqDTO> codes;    // import할 코드 목록
	private List<VolumeReqDTO> volumes;    // 마운트할 볼륨 목록 (볼륨명, 마운트할 경로)
	private List<PortReqDTO> ports;    // 노드 포토 목록 (포트명, 포트번호)
	private List<EnvReqDTO> envs;	// 환경변수 목록 (변수명, 값)
	private String command;	// 실행할 명령어
	private WorkloadType workloadType;	// 워크로드 타입(BATCH, INTERACTIVE, SERVICE)
	private VolumeSelectionType volumeSelectionType;
	private int gpuRequest;
	private float cpuRequest;
	private float memRequest;

	public CreateWorkloadReqDTO(String name, String description, String creatorName, String workspace, String creator,
		String image, List<CodeReqDTO> codes, List<VolumeReqDTO> volumes, List<PortReqDTO> ports,
		List<EnvReqDTO> envs, String command, WorkloadType workloadType,
		int gpuRequest, float cpuRequest, float memRequest) {
		super(name, description, creatorName, creator);
		this.workspace = workspace;
		this.image = image;
		this.codes = codes;
		this.volumes = volumes;
		this.ports = ports;
		this.envs = envs;
		this.command = command;
		this.workloadType = workloadType;
		this.gpuRequest = gpuRequest;
		this.cpuRequest = cpuRequest;
		this.memRequest = memRequest;
	}

	public JobVO toJobVO() {
		return JobVO.builder()
			.workspace(this.workspace)
			.name(this.getName())
			.description(this.getDescription())
			.creatorName(this.getCreatorName())
			.creator(this.getCreator())
			.image(this.image)
			.codes(this.codes.stream().map(CodeReqDTO::toJobCodeVO).collect(Collectors.toList()))
			.volumes(this.volumes.stream().map(VolumeReqDTO::toJobVolumeVO).collect(Collectors.toList()))
			.ports(this.ports.stream().map(PortReqDTO::toJobPortVO).collect(Collectors.toList()))
			.envs(this.envs.stream().map(EnvReqDTO::toJobEnvVO).collect(Collectors.toList()))
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.build();
	}
}