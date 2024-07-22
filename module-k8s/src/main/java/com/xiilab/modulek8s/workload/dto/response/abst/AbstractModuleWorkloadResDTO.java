package com.xiilab.modulek8s.workload.dto.response.abst;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.workload.dto.response.ModuleCodeResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleEnvResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModulePortResDTO;
import com.xiilab.modulek8s.workload.enums.SchedulingType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractModuleWorkloadResDTO {
	protected String uid;                          // 워크로드 고유 ID
	protected String name;                         // 사용자가 입력한 워크로드의 이름
	protected String resourceName;                 // 워크로드 실제 이름
	protected String description;                  // 워크로드 설명
	protected String creatorId;                    // 생성자 ID
	protected String creatorUserName;              // 생성자 username(unique)
	protected String creatorFullName;              // 생성자 fullName(unique)
	protected String workspaceResourceName;        // 워크스페이스 리소스 이름
	protected String workspaceName;                // 워크스페이스 이름
	protected WorkloadType type;                   // 워크로드 타입
	protected String image;                        // 사용할 image
	protected LocalDateTime createdAt;             // 워크로드 생성일시
	protected LocalDateTime statedAt;              // 워크로드 시작일시
	protected LocalDateTime deletedAt;             // 워크로드 종료일시
	protected SchedulingType schedulingType;       // 스케줄링 방식
	protected List<ModuleEnvResDTO> envs;          // env 정의
	protected List<ModulePortResDTO> ports;        // port 정의
	protected List<ModuleCodeResDTO> codes;        // port 정의
	protected Map<Long, String> datasetMountPathMap = new HashMap<>();    // dataset - mount path 맵
	protected Map<Long, String> modelMountPathMap = new HashMap<>(); // model - mount path 맵
	protected String command;                      // 워크로드 명령
	protected WorkloadStatus status;               // 워크로드 status
	protected boolean isPinYN;                     // PIN YN
	protected AgeDTO age;                          // 워크로드 경과시간
	protected Integer remainTime;                      // 잔여시간
	protected String datasetIds;
	protected String modelIds;
	protected String codeIds;
	protected Long imageId;
	protected ImageType imageType;
	protected Long imageCredentialId;
	protected boolean canBeDeleted;
	protected String ide;
	protected String workingDir;
	protected Map<String, String> parameter;
	// 최초 예측 시간
	protected String estimatedInitialTime;
	// 실시간 예측 시간
	protected String estimatedRemainingTime;
	private Map<String, Map<String, String>> codeMountPathMap;        // model - mount path 맵
	@Setter
	private String startTime;    // 파드 실행시간
	private GPUType gpuType;
	private String gpuName;
	private String nodeName;
	private Integer gpuOnePerMemory;
	private Integer resourcePresetId;

	protected AbstractModuleWorkloadResDTO(HasMetadata hasMetadata) {
		if (hasMetadata != null) {
			uid = hasMetadata.getMetadata().getUid();
			name = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField());
			resourceName = hasMetadata.getMetadata().getName();
			description = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.DESCRIPTION.getField());
			creatorId = hasMetadata.getMetadata().getLabels().get(LabelField.CREATOR_ID.getField());
			creatorUserName = hasMetadata.getMetadata()
				.getAnnotations()
				.get(AnnotationField.CREATOR_USER_NAME.getField());
			creatorFullName = hasMetadata.getMetadata()
				.getAnnotations()
				.get(AnnotationField.CREATOR_FULL_NAME.getField());
			workspaceResourceName = hasMetadata.getMetadata().getNamespace();
			workspaceName = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.WORKSPACE_NAME.getField());
			createdAt = DateUtils.convertK8sUtcTimeString(hasMetadata.getMetadata().getCreationTimestamp());
			age = createdAt != null ? new AgeDTO(createdAt) : null;
			type = getType();
			datasetIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.DATASET_IDS.getField());
			modelIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.MODEL_IDS.getField());
			codeIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CODE_IDS.getField());
			imageType = ImageType.valueOf(
				hasMetadata.getMetadata().getAnnotations().get(AnnotationField.IMAGE_TYPE.getField()));
			imageCredentialId = !StringUtils.hasText(hasMetadata.getMetadata()
				.getAnnotations()
				.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) ? null :
				Long.parseLong(hasMetadata.getMetadata()
					.getAnnotations()
					.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField()));
			imageId = StringUtils.hasText(
				hasMetadata.getMetadata().getAnnotations().get(AnnotationField.IMAGE_ID.getField())) ?
				Long.valueOf(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.IMAGE_ID.getField())) :
				null;
			parameter = getParameterMap(hasMetadata.getMetadata().getAnnotations());
			gpuType = StringUtils.hasText(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_TYPE.getField()))?
				GPUType.valueOf(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_TYPE.getField())) : null;
			gpuName = StringUtils.hasText(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_NAME.getField()))?
				hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_NAME.getField()) : null;
			nodeName = StringUtils.hasText(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NODE_NAME.getField()))?
				hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NODE_NAME.getField()) : null;
			gpuOnePerMemory = !ValidUtils.isNullOrEmpty(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_ONE_PER_MEMORY.getField()))?
				Integer.parseInt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.GPU_ONE_PER_MEMORY.getField())) : null;
			resourcePresetId = !ValidUtils.isNullOrEmpty(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.RESOURCE_PRESET_ID.getField()))?
				Integer.parseInt(hasMetadata.getMetadata().getAnnotations().get(AnnotationField.RESOURCE_PRESET_ID.getField())) : null;

		} else {
			throw new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO);
		}
	}

	public void updateCanBeDeleted(String creator, Set<String> ownerWorkspace) {
		if (this.creatorId.equals(creator) || ownerWorkspace.contains(this.workspaceResourceName)) {
			this.canBeDeleted = true;
		}
	}

	public void updatePinYN(boolean pinYN) {
		this.isPinYN = pinYN;
	}

	// 소스코드 환경변수에 저장된 값 respone
	protected List<ModuleCodeResDTO> initializeCodesInfo(List<Container> initContainers) {
		List<ModuleCodeResDTO> moduleCodeResDTOS = initContainers.stream()
			.map(initContainer -> new ModuleCodeResDTO(initContainer.getEnv())).toList();
		// code mountpath map 추가
		initializeCodeMountPath(moduleCodeResDTOS);
		return moduleCodeResDTOS;
	}

	protected void initializeCodeMountPath(List<ModuleCodeResDTO> codes) {
		this.codeMountPathMap = new HashMap<>();
		for (ModuleCodeResDTO code : codes) {
			codeMountPathMap.computeIfAbsent(code.getRepositoryUrl(), k -> new HashMap<>());
			Map<String, String> pathMap = codeMountPathMap.get(code.getRepositoryUrl());
			pathMap.put("mountPath", code.getMountPath());
			pathMap.put("branch", code.getBranch());
			pathMap.put("command", code.getCommand());
		}
	}

	protected void initializeVolumeMountPath(Map<String, String> annotations) {
		annotations.entrySet().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.startsWith("ds-")) {
				this.datasetMountPathMap.put(Long.parseLong(key.split("-")[1]), value);
			} else if (key.startsWith("md-")) {
				this.modelMountPathMap.put(Long.parseLong(key.split("-")[1]), value);
			}
		});
	}

	public void updatePort(String nodeIp, Service service) {
		if (Objects.nonNull(service) && Objects.nonNull(service.getSpec().getPorts())) {
			List<ServicePort> servicePorts = service.getSpec().getPorts();
			this.ports = servicePorts.stream().map(servicePort -> ModulePortResDTO.builder()
				.name(servicePort.getName())
				.originPort(servicePort.getPort())
				.url(String.format("%s:%s", nodeIp, servicePort.getNodePort()))
				.build()).toList();
		}
	}

	public void updatePort(List<ModulePortResDTO> modulePortResDTOS) {
		this.ports = modulePortResDTOS;
	}

	public Map<String, String> getEnvsMap() {
		Map<String, String> envsMap = new HashMap<>();
		if (CollectionUtils.isEmpty(this.envs)) {
			return envsMap;
		}

		for (ModuleEnvResDTO env : envs) {
			String name = StringUtils.hasText(env.getName()) ? env.getName() : "";
			String value = StringUtils.hasText(env.getValue()) ? env.getValue() : "";

			envsMap.put(name, value);
		}

		return envsMap;
	}

	public Map<String, Integer> getPortsMap() {
		Map<String, Integer> portsMap = new HashMap<>();
		if (CollectionUtils.isEmpty(this.ports)) {
			return portsMap;
		}
		for (ModulePortResDTO port : ports) {
			String name = StringUtils.hasText(port.getName()) ? port.getName() : "";
			int value = port.getOriginPort() != null ? port.getOriginPort() : 0;

			portsMap.put(name, value);
		}

		return portsMap;
	}


	public abstract WorkloadType getType();
}
