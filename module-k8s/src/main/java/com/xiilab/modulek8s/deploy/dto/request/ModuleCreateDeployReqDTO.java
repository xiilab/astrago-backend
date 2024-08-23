package com.xiilab.modulek8s.deploy.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.deploy.vo.DeployVO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleCreateDeployReqDTO extends K8SResourceReqDTO {
	protected String workspace;    // 워크스페이스명
	protected ImageType imageType;    // 이미지 타입(빌트인, Dockerhub)
	protected ModuleImageReqDTO image;    // 이미지명
	protected List<ModulePortReqDTO> ports;    // 노드 포토 목록 (포트명, 포트번호)
	protected List<ModuleEnvReqDTO> envs;    // 환경변수 목록 (변수명, 값)
	protected String workingDir;    // 명령어를 실행 할 path
	protected String command;    // 실행할 명령어
	protected WorkloadType workloadType;    // 워크로드 타입(BATCH, INTERACTIVE, DEPLOY)
	protected Integer gpuRequest;
	protected Float cpuRequest;
	protected Float memRequest;
	protected String imageSecretName;
	protected String nodeName;
	protected String gpuName;
	protected String modelVersion;
	protected GPUType gpuType;
	protected Integer gpuOnePerMemory;
	protected Integer resourcePresetId;
	protected Integer replica;
	protected List<ModuleVolumeReqDTO> volumes;
	protected long deployModelId;
	protected DeployType deployType;
	protected String initContainerUrl;

	public DeployVO toDeployVO(String workspaceName) {
		initializeCollection();
		return DeployVO.builder()
			.workspace(this.workspace)
			.workspaceName(workspaceName)
			.name(this.getName())
			.description(this.getDescription())
			.creatorId(this.getCreatorId())
			.creatorUserName(this.getCreatorUserName())
			.creatorFullName(this.getCreatorFullName())
			.image(this.image.toJobImageVO(this.workspace))
			.volumes(this.volumes != null ? this.volumes.stream().map(ModuleVolumeReqDTO::toJobVolumeVO).toList() : null)
			.ports(this.ports.stream().map(ModulePortReqDTO::toJobPortVO).toList())
			.envs(this.envs.stream().map(ModuleEnvReqDTO::toJobEnvVO).toList())
			.command(this.command)
			.workloadType(this.workloadType)
			.cpuRequest(this.cpuRequest)
			.gpuRequest(this.gpuRequest)
			.memRequest(this.memRequest)
			.secretName(this.imageSecretName)
			.nodeName(this.nodeName)
			.gpuType(this.gpuType)
			.gpuName(this.gpuName)
			.gpuOnePerMemory(this.gpuOnePerMemory)
			.resourcePresetId(this.resourcePresetId)
			.replica(this.replica)
			.deployType(this.deployType)
			.deployModelId(this.deployModelId)
			.modelVersion(this.modelVersion)
			.workingDir(this.workingDir)
			.initContainerUrl(this.initContainerUrl)
			.build();
	}
	protected void initializeCollection() {
		this.ports = getListIfNotEmpty(this.ports);
		this.envs = getListIfNotEmpty(this.envs);
	}
	protected <T> List<T> getListIfNotEmpty(List<T> list) {
		return CollectionUtils.isEmpty(list) ? new ArrayList<>() : list;
	}
	public CredentialVO toCredentialVO() {
		JobImageVO jobImageVO = this.image.toJobImageVO(workspace);
		return jobImageVO.credentialVO();
	}
	public void setImageSecretName(String imageSecretName) {
		this.imageSecretName = imageSecretName;
	}
	public void modifyImage(ModuleImageReqDTO image){
		this.image = image;
	}
}
