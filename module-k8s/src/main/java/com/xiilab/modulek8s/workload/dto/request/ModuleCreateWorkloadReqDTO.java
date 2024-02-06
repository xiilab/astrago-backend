package com.xiilab.modulek8s.workload.dto.request;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleCreateWorkloadReqDTO extends K8SResourceReqDTO {
	private String workspace;    // 워크스페이스명
	private WorkloadType workloadType;    // 워크로드 타입(BATCH, INTERACTIVE)
	private ModuleImageReqDTO image;    // 이미지명
	private List<ModuleCodeReqDTO> codes;    // import할 코드 목록
	private List<ModuleVolumeReqDTO> volumes;    // 마운트할 볼륨 목록 (볼륨명, 마운트할 경로)
	private List<ModulePortReqDTO> ports;    // 노드 포토 목록 (포트명, 포트번호)
	private List<ModuleEnvReqDTO> envs;    // 환경변수 목록 (변수명, 값)
	private String command;    // 실행할 명령어
	private int gpuRequest;
	private float cpuRequest;
	private float memRequest;
	private String imageSecretName;

	public CredentialVO toCredentialVO() {
		JobImageVO jobImageVO = this.image.toJobImageVO(workspace);
		return jobImageVO.credentialVO();
	}

	public BatchJobVO toBatchJobVO() {
		initializeCollection();

		return BatchJobVO.builder()
			.workspace(this.workspace)
			.name(this.getName())
			.description(this.getDescription())
			.creatorName(this.getCreatorName())
			.creator(this.getCreator())
			.secretName(this.imageSecretName)
			.image(this.image.toJobImageVO(this.workspace))
			.codes(this.codes.stream().map(codReqDTO -> codReqDTO.toJobCodeVO(workspace)).toList())
			.volumes(this.volumes.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.build();
	}

	public InteractiveJobVO toInteractiveJobVO() {
		initializeCollection();

		return InteractiveJobVO.builder()
			.workspace(this.workspace)
			.name(this.getName())
			.description(this.getDescription())
			.creatorName(this.getCreatorName())
			.creator(this.getCreator())
			.secretName(this.imageSecretName)
			.image(this.image.toJobImageVO(this.workspace))
			.codes(this.codes.stream().map(codReqDTO -> codReqDTO.toJobCodeVO(workspace)).toList())
			.volumes(this.volumes.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.build();
	}

	private void initializeCollection() {
		this.codes = getNotEmptyListIfNotEmpty(this.codes);
		this.volumes = getNotEmptyListIfNotEmpty(this.volumes);
		this.ports = getNotEmptyListIfNotEmpty(this.ports);
		this.envs = getNotEmptyListIfNotEmpty(this.envs);
	}

	private <T> List<T> getNotEmptyListIfNotEmpty(List<T> list) {
		return CollectionUtils.isEmpty(list) ? new ArrayList<>() : list;
	}

	public void setImageSecretName(String imageSecretName) {
		this.imageSecretName = imageSecretName;
	}
}
