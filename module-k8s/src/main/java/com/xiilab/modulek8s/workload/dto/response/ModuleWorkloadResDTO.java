package com.xiilab.modulek8s.workload.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class ModuleWorkloadResDTO {
	String uid;                          // 워크로드 고유 ID
	String name;                         // 워크로드 이름
	String description;                  // 워크로드 설명
	String creator;                      // 생성자 ID
	String workspaceResourceName;        // 워크스페이스 리소스 이름
	String workspaceName;                // 워크스페이스 이름
	WorkloadType type;                   // 워크로드 타입
	String image;                        // 사용할 image
	String gpuRequest;                   // 워크로드 gpu 요청량
	String cpuRequest;                   // 워크로드 cpu 요청량
	String memRequest;                   // 워크로드 mem 요청량
	LocalDateTime createdAt;             // 워크로드 생성일시
	SchedulingType schedulingType;       // 스케줄링 방식
	List<ModuleEnvResDTO> envs;          // env 정의
	List<ModulePortResDTO> ports;        // port 정의
	String command;                      // 워크로드 명령
	WorkloadStatus status;               // 워크로드 status
	private boolean isPinYN;            // PIN YN
	AgeDTO age;							 // 워크로드 경과시간

	protected ModuleWorkloadResDTO(HasMetadata hasMetadata) {
		uid = hasMetadata.getMetadata().getUid();
		name = hasMetadata.getMetadata().getName();
		description = hasMetadata.getMetadata().getAnnotations().get("description");
		creator = hasMetadata.getMetadata().getLabels().get("creator");
		workspaceResourceName = hasMetadata.getMetadata().getNamespace();
		workspaceName = hasMetadata.getMetadata().getAnnotations().get("name");
		createdAt = DateUtils.convertK8sUtcTimeString(hasMetadata.getMetadata().getCreationTimestamp());
		age = DateUtils.getAge(createdAt);
		type = getType();
	}

	public abstract WorkloadType getType();
}
