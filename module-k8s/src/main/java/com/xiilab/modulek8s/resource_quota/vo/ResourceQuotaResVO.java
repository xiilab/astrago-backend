package com.xiilab.modulek8s.resource_quota.vo;

import static com.xiilab.modulek8s.resource_quota.enumeration.ResourceQuotaKey.*;

import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResourceQuotaResVO extends K8SResourceResVO {
	private String namespace;
	private int reqCPU;
	private int reqMEM;
	private int reqGPU;
	private int reqDisk;
	private int limitCPU;
	private int limitMEM;
	private int limitGPU;

	public ResourceQuotaResVO(ResourceQuota resourceQuota) {
		super(resourceQuota);
		this.namespace = resourceQuota.getMetadata().getNamespace();
		Map<String, Quantity> hard = resourceQuota.getSpec().getHard();
		this.reqCPU = getAmountFromMap(hard, REQUEST_CPU_KEY.getKey());
		this.reqMEM = getAmountFromMap(hard, REQUEST_MEMORY_KEY.getKey());
		this.reqGPU = getAmountFromMap(hard, REQUEST_GPU_KEY.getKey());
		this.reqDisk = getAmountFromMap(hard, REQUEST_DISK_KEY.getKey());
		this.limitCPU = getAmountFromMap(hard, LIMITS_CPU_KEY.getKey());
		this.limitMEM = getAmountFromMap(hard, LIMITS_MEMORY_KEY.getKey());
		this.limitGPU = getAmountFromMap(hard, LIMITS_GPU_KEY.getKey());
	}

	/**
	 * Get integer value from 'hard' map by specific key.
	 * If the key does not exist, return 0.
	 */
	private int getAmountFromMap(Map<String, Quantity> hard, String key) {
		Quantity quantity = hard.get(key);
		return quantity != null ? Integer.parseInt(quantity.getAmount()) : 0;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.RESOURCE_QUOTA;
	}
}
