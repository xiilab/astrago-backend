package com.xiilab.modulek8s.workload.dto.response;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class ModuleWorkloadResDTO {
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
	protected String gpuRequest;                   // 워크로드 gpu 요청량
	protected String cpuRequest;                   // 워크로드 cpu 요청량
	protected String memRequest;                   // 워크로드 mem 요청량
	protected LocalDateTime createdAt;             // 워크로드 생성일시
	protected LocalDateTime statedAt;			   // 워크로드 시작일시
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
	protected int remainTime;                      // 잔여시간
	protected String datasetIds;
	protected String modelIds;
	protected String codeIds;
	protected String imageId;
	protected String imageType;
	protected Long imageCredentialId;
	protected boolean canBeDeleted;
	protected String ide;
	protected String workingDir;
	protected Map<String,String> parameter;
	// 최초 예측 시간
	LocalDateTime estimatedInitialTime;
	// 실시간 예측 시간
	LocalDateTime estimatedRemainingTime;
	@Setter
	private String startTime;	// 파드 실행시간
	protected ModuleWorkloadResDTO(HasMetadata hasMetadata) {
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
			age = new AgeDTO(createdAt);
			type = getType();
			datasetIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.DATASET_IDS.getField());
			modelIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.MODEL_IDS.getField());
			codeIds = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CODE_IDS.getField());
			imageType = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.IMAGE_TYPE.getField());
			imageCredentialId = !StringUtils.hasText(hasMetadata.getMetadata()
				.getAnnotations()
				.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField())) ? null :
				Long.parseLong(hasMetadata.getMetadata()
					.getAnnotations()
					.get(AnnotationField.IMAGE_CREDENTIAL_ID.getField()));
			imageId = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.IMAGE_ID.getField());
			parameter = getParameterMap(hasMetadata.getMetadata().getAnnotations());
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

	public abstract WorkloadType getType();

	// 소스코드 환경변수에 저장된 값 respone
	protected List<ModuleCodeResDTO> initializeCodesInfo(List<Container> initContainers) {
		return initContainers.stream()
			.map(initContainer -> new ModuleCodeResDTO(initContainer.getEnv())).toList();
	}

	protected void initializeResources(Map<String, Quantity> resourceRequests) {
		Quantity gpu = resourceRequests.get("nvidia.com/gpu");
		Quantity cpu = resourceRequests.get("cpu");
		Quantity memory = resourceRequests.get("memory");
		this.gpuRequest = gpu != null ? gpu.getAmount() : "0";
		this.cpuRequest = cpu != null ? String.valueOf(K8sInfoPicker.convertQuantity(cpu)) : "0";
		this.memRequest = memory != null ? String.valueOf(K8sInfoPicker.convertQuantity(memory)) : "0";
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
}
