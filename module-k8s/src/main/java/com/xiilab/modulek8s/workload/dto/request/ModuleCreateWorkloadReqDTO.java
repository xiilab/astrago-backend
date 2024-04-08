package com.xiilab.modulek8s.workload.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleCreateWorkloadReqDTO extends K8SResourceReqDTO {
	private String workspace;    // 워크스페이스명
	private ImageType imageType;    // 이미지 타입(빌트인, Dockerhub)
	private ModuleImageReqDTO image;    // 이미지명
	private List<ModuleCodeReqDTO> codes;    // import할 코드 목록
	private List<ModuleVolumeReqDTO> datasets;    // 마운트할 데이터셋 볼륨 목록 (볼륨명, 마운트할 경로)
	private List<ModuleVolumeReqDTO> models;    // 마운트할 모델 볼륨 목록 (볼륨명, 마운트할 경로)
	private List<ModulePortReqDTO> ports;    // 노드 포토 목록 (포트명, 포트번호)
	private List<ModuleEnvReqDTO> envs;    // 환경변수 목록 (변수명, 값)
	private String workingDir;	// 명령어를 실행 할 path
	private String command;    // 실행할 명령어
	private Map<String,String> args; // 사용자가 입력한 args
	private WorkloadType workloadType;    // 워크로드 타입(BATCH, INTERACTIVE, SERVICE)
	private Integer gpuRequest;
	private Float cpuRequest;
	private Float memRequest;
	private String imageSecretName;
	private String ide;

	public CredentialVO toCredentialVO() {
		JobImageVO jobImageVO = this.image.toJobImageVO(workspace);
		return jobImageVO.credentialVO();
	}

	public BatchJobVO toBatchJobVO(String workspaceName) {
		initializeCollection();

		return BatchJobVO.builder()
			.workspace(this.workspace)
			.workspaceName(workspaceName)
			.name(this.getName())
			.description(this.getDescription())
			.creatorId(this.getCreatorId())
			.creatorUserName(this.getCreatorUserName())
			.creatorFullName(this.getCreatorFullName())
			.image(this.image.toJobImageVO(this.workspace))
			.codes(this.codes.stream().map(codeDTO -> codeDTO.toJobCodeVO(workspace)).toList())
			.datasets(this.datasets.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.models(this.models.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.workingDir(this.workingDir)
			.command(this.command)
			.args(args)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.secretName(this.imageSecretName)
			.build();
	}

	public InteractiveJobVO toInteractiveJobVO(String workspaceName) {
		initializeCollection();

		return InteractiveJobVO.builder()
			.workspace(this.workspace)
			.workspaceName(workspaceName)
			.name(this.getName())
			.description(this.getDescription())
			.creatorId(this.getCreatorId())
			.creatorUserName(this.getCreatorUserName())
			.creatorFullName(this.getCreatorFullName())
			.image(this.image.toJobImageVO(this.workspace))
			.codes(this.codes.stream().map(codeDTO -> codeDTO.toJobCodeVO(workspace)).toList())
			.datasets(this.datasets.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.models(this.models.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList())
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.secretName(this.imageSecretName)
			.ide(this.ide)
			.build();
	}

	private void initializeCollection() {
		this.codes = getListIfNotEmpty(this.codes);
		this.datasets = getListIfNotEmpty(this.datasets);
		this.models = getListIfNotEmpty(this.models);
		this.ports = getListIfNotEmpty(this.ports);
		this.envs = getListIfNotEmpty(this.envs);
	}

	private <T> List<T> getListIfNotEmpty(List<T> list) {
		return CollectionUtils.isEmpty(list) ? new ArrayList<>() : list;
	}

	public void setImageSecretName(String imageSecretName) {
		this.imageSecretName = imageSecretName;
	}
}
