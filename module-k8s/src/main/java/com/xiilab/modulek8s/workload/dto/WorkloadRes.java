package com.xiilab.modulek8s.workload.dto;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
@Getter
@SuperBuilder
public abstract class WorkloadRes {
	String uid;		//워크로드 고유 ID
	String name;		// 워크로드 이름
	String description;		// 워크로드 설명
	String creatorId;		//생성자 ID
	String workspace;		//워크스페이스
	WorkloadType type;		// 워크로드 타입
	String image;		//사용할 image
	int gpuRequest;		// 워크로드 gpu 요청량
	int cpuRequest;		// 워크로드 cpu 요청량
	int memRequest;		// 워크로드 mem 요청량
	SchedulingType schedulingType;		// 스케줄링 방식
	Map<String, String> env;		//env 정의
	List<PortResDTO> port;		//port 정의
	String command;		// 워크로드 명령

	protected WorkloadRes(HasMetadata hasMetadata) {
		uid = hasMetadata.getMetadata().getUid();
		name = hasMetadata.getMetadata().getName();
		description = hasMetadata.getMetadata().getAnnotations().get("description");
		creatorId = hasMetadata.getMetadata().getAnnotations().get("creatorId");
		workspace = hasMetadata.getMetadata().getNamespace();
		type = getType();
	}

	public abstract WorkloadRes convertResDTO(HasMetadata hasMetadata);
	public abstract WorkloadType getType();
}
