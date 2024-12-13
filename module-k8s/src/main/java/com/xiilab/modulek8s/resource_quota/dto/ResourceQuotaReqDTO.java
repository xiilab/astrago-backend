package com.xiilab.modulek8s.resource_quota.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaReqVO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResourceQuotaReqDTO extends K8SResourceReqDTO {
	private String namespace;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;

	public ResourceQuotaReqVO convertToResourceQuota() {
		return ResourceQuotaReqVO.builder()
			.name(getName())
			.namespace(getNamespace())
			.description(getDescription())
			.creatorId(getCreatorId())
			.creatorUserName(getCreatorUserName())
			.creatorFullName(getCreatorFullName())
			.reqCpu(reqCPU)
			.reqMem(reqMEM)
			.reqGpu(reqGPU)
			.limitCpu(reqCPU)
			.limitMem(reqMEM)
			.limitGpu(reqGPU)
			.build();
	}
}
