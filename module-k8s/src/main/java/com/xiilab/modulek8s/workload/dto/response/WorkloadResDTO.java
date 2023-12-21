package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class WorkloadRes extends K8SResourceResVO {
	String workspace;		//워크스페이스
	WorkloadType workloadType;        // 워크로드 타입
	String image;		//사용할 image
	String gpuRequest;		// 워크로드 gpu 요청량
	String cpuRequest;		// 워크로드 cpu 요청량
	String memRequest;		// 워크로드 mem 요청량
	SchedulingType schedulingType;		// 스케줄링 방식
	List<EnvResDTO> envs;		//env 정의
	List<PortResDTO> ports;		//port 정의
	String command;		// 워크로드 명령

	protected WorkloadRes(HasMetadata hasMetadata) {
        super(hasMetadata);
        workspace = hasMetadata.getMetadata().getNamespace();
	}

	public abstract WorkloadRes convertResDTO(HasMetadata hasMetadata);

	public abstract WorkloadType getWorkloadType();
}