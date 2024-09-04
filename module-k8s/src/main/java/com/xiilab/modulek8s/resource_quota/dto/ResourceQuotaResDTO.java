package com.xiilab.modulek8s.resource_quota.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceResDTO;
import com.xiilab.modulek8s.common.enumeration.ResourceType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResourceQuotaResDTO extends K8SResourceResDTO {
	private String namespace;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;
	private int reqDISK;
	private int limitCPU;
	private int limitMEM;
	private int limitGPU;
	private int useCPU;
	private int useMEM;
	private int useGPU;

	@Override
	protected ResourceType getType() {
		return ResourceType.RESOURCE_QUOTA;
	}
}
