package com.xiilab.modulek8s.resource_quota.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaReqVO;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaResVO;

@Repository
public interface ResourceQuotaRepo {
	void createResourceQuotas(ResourceQuotaReqVO resourceQuotaReqVO);

	void deleteResourceQuotas(String name, String namespace);

	ResourceQuotaResVO getResourceQuotas(String namespace);

	void updateResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq);
	List<ResourceQuotaResVO> getResourceQuotasList();

}
