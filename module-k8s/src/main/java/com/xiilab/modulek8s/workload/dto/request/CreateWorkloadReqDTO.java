package com.xiilab.modulek8s.workload.dto.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateWorkloadReqDTO extends K8SResourceReqDTO {
	protected String workspace;    // 워크스페이스명
	protected ImageType imageType;    // 이미지 타입(빌트인, Dockerhub)
	protected ModuleImageReqDTO image;    // 이미지명
	protected List<ModuleCodeReqDTO> codes;    // import할 코드 목록
	// TODO 삭제 예정
	// protected List<ModuleVolumeReqDTO> datasets;    // 마운트할 데이터셋 볼륨 목록 (볼륨명, 마운트할 경로)
	// protected List<ModuleVolumeReqDTO> models;    // 마운트할 모델 볼륨 목록 (볼륨명, 마운트할 경로)
	protected List<ModuleVolumeReqDTO> volumes;    // 마운트할 볼륨 목록 (볼륨명, 마운트할 경로)
	protected List<ModulePortReqDTO> ports;    // 노드 포토 목록 (포트명, 포트번호)
	protected List<ModuleEnvReqDTO> envs;    // 환경변수 목록 (변수명, 값)
	protected String workingDir;    // 명령어를 실행 할 path
	protected String command;    // 실행할 명령어
	protected Map<String, String> parameter; // 사용자가 입력한 parameter
	protected WorkloadType workloadType;    // 워크로드 타입(BATCH, INTERACTIVE, SERVICE)
	protected Integer gpuRequest;
	protected Float cpuRequest;
	protected Float memRequest;
	protected String imageSecretName;
	protected String ide;
	protected String initContainerUrl;
	protected String nodeName;
	protected String gpuName;
	protected GPUType gpuType;
	protected Integer gpuOnePerMemory;
	protected Integer resourcePresetId;
	protected String jobName;
	protected Set<Long> labelIds;

	protected void initializeCollection() {
		this.codes = initCollectionIfNotEmpty(this.codes, ArrayList::new);
		// TODO 삭제 예정
		// this.datasets = getListIfNotEmpty(this.datasets);
		// this.models = getListIfNotEmpty(this.models);
		this.volumes = initCollectionIfNotEmpty(this.volumes, ArrayList::new);
		this.ports = initCollectionIfNotEmpty(this.ports, ArrayList::new);
		this.envs = initCollectionIfNotEmpty(this.envs, ArrayList::new);
		this.labelIds = initCollectionIfNotEmpty(this.labelIds, HashSet::new);
	}

	protected <C extends Collection<?>> C initCollectionIfNotEmpty(C collection, Supplier<? extends C> supplier) {
		return (collection == null || collection.isEmpty()) ? supplier.get() : collection;
	}

	public void setImageSecretName(String imageSecretName) {
		this.imageSecretName = imageSecretName;
	}

	public CredentialVO toCredentialVO() {
		JobImageVO jobImageVO = this.image.toJobImageVO(workspace);
		return jobImageVO.credentialVO();
	}

	protected String getUniqueResourceName() {
		return ResourceType.WORKLOAD.getName() + "-" + UUID.randomUUID();
	}
}
