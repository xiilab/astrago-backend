package com.xiilab.modulek8s.resource_quota.vo;

import static com.xiilab.modulek8s.resource_quota.enumeration.ResourceQuotaKey.*;

import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResourceQuotaResVO extends K8SResourceResVO {
	private String namespace;
	private float reqCPU;
	private float reqMEM;
	private float reqGPU;
	private float useCPU;
	private float useMEM;
	private float useGPU;
	private float limitCPU;
	private float limitMEM;
	private float limitGPU;

	public ResourceQuotaResVO(ResourceQuota resourceQuota) {
		super(resourceQuota);
		ResourceQuotaStatus resourceStatus = resourceQuota.getStatus();
		this.namespace = resourceQuota.getMetadata().getNamespace();
		this.reqCPU = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(REQUEST_CPU_KEY.getKey()));
		this.limitCPU = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(LIMITS_CPU_KEY.getKey()));
		this.useCPU = K8sInfoPicker.convertQuantity(resourceStatus.getUsed().get(REQUEST_CPU_KEY.getKey()));
		this.reqMEM = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(REQUEST_MEMORY_KEY.getKey()));
		this.limitMEM = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(LIMITS_MEMORY_KEY.getKey()));
		this.useMEM = K8sInfoPicker.convertQuantity(resourceStatus.getUsed().get(REQUEST_MEMORY_KEY.getKey()));
		this.reqGPU = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(REQUEST_GPU_KEY.getKey()));
		this.limitGPU = K8sInfoPicker.convertQuantity(resourceStatus.getHard().get(LIMITS_GPU_KEY.getKey()));
		this.useGPU = K8sInfoPicker.convertQuantity(resourceStatus.getUsed().get(REQUEST_GPU_KEY.getKey()));
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
