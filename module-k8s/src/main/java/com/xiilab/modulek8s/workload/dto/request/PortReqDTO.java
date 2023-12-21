package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.vo.JobPortVO;

public record PortReqDTO(
	String name,	// 포트명
	Integer port	// 포트번호
) {
	public JobPortVO toJobPortVO() {
		return new JobPortVO(name, port);
	}
}