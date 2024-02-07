package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.vo.JobEnvVO;

public record ModuleEnvReqDTO(
	String name,    // 변수명
	String value    // 값
	) {
	public JobEnvVO toJobEnvVO() {
		return new JobEnvVO(name, value);
	}
}
