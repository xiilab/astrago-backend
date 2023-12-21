package com.xiilab.modulek8s.workload.dto.response;

import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public abstract class WorkloadResDTO extends K8SResourceResVO {
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

	protected WorkloadResDTO(HasMetadata hasMetadata) {
        super(hasMetadata);
        workspace = hasMetadata.getMetadata().getNamespace();
	}

	public abstract WorkloadType getWorkloadType();
}