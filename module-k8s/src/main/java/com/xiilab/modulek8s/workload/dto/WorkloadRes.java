package com.xiilab.modulek8s.workload.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
@Getter
@SuperBuilder
public abstract class WorkloadRes extends K8SResourceResVO {
	String workspace;		//워크스페이스
	String image;		//사용할 image
	int gpuRequest;		// 워크로드 gpu 요청량
	int cpuRequest;		// 워크로드 cpu 요청량
	int memRequest;		// 워크로드 mem 요청량
	SchedulingType schedulingType;		// 스케줄링 방식
	Map<String, String> env;		//env 정의
	List<PortResDTO> port;		//port 정의
	String command;		// 워크로드 명령
	WorkloadType workloadType;

	protected WorkloadRes(HasMetadata hasMetadata) {
        super(hasMetadata);
        workspace = hasMetadata.getMetadata().getNamespace();
	}

	public abstract WorkloadRes convertResDTO(HasMetadata hasMetadata);

	public abstract WorkloadType getWorkloadType();
}
