package com.xiilab.modulek8s.resource_quota.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.dto.TotalResourceQuotaDTO;

@Service
public interface ResourceQuotaService {
	void createResourceQuotas(ResourceQuotaReqDTO resourceQuotaReqDTO);

	void deleteResourceQuotas(String name, String workspace);

	ResourceQuotaResDTO getResourceQuotas(String namespace);
	void updateResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq);
	List<ResourceQuotaResDTO> getResourceQuotasList();
	TotalResourceQuotaDTO getTotalResourceQuota();
}
