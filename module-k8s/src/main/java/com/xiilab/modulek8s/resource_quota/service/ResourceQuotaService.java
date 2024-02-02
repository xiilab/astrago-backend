package com.xiilab.modulek8s.resource_quota.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;

@Service
public interface ResourceQuotaService {
	void createResourceQuotas(ResourceQuotaReqDTO resourceQuotaReqDTO);

	void deleteResourceQuotas(String name, String workspace);

	ResourceQuotaResDTO getResourceQuotas(String namespace);
	void updateResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq);
}
