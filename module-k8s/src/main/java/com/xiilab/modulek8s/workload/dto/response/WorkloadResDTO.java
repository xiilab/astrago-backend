package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
@Getter
@SuperBuilder
public abstract class WorkloadResDTO {
	String uid;		//워크로드 고유 ID
	String name;		// 워크로드 이름
	String description;		// 워크로드 설명
	String creatorId;		//생성자 ID
	String workspace;		//워크스페이스
	WorkloadType type;		// 워크로드 타입
	String image;		//사용할 image
	String gpuRequest;		// 워크로드 gpu 요청량
	String cpuRequest;		// 워크로드 cpu 요청량
	String memRequest;		// 워크로드 mem 요청량
	SchedulingType schedulingType;		// 스케줄링 방식
	List<EnvResDTO> envs;		//env 정의
	List<PortResDTO> ports;		//port 정의
	String command;		// 워크로드 명령

	protected WorkloadResDTO(HasMetadata hasMetadata) {
		uid = hasMetadata.getMetadata().getUid();
		name = hasMetadata.getMetadata().getName();
		description = hasMetadata.getMetadata().getAnnotations().get("description");
		creatorId = hasMetadata.getMetadata().getAnnotations().get("creatorId");
		workspace = hasMetadata.getMetadata().getNamespace();
		type = getType();
	}

	public abstract WorkloadType getType();
}