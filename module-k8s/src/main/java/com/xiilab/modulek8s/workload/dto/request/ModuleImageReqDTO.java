package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.vo.JobImageVO;

public record ModuleImageReqDTO(
	String name,
	String tag
){
	public JobImageVO toJobImageVO() {
		return new JobImageVO(name, tag);
	}
}
