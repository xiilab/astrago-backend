package com.xiilab.modulek8s.resource_quota.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.repository.ResourceQuotaRepo;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaResVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceQuotaServiceImpl implements ResourceQuotaService {
	private final ResourceQuotaRepo resourceQuotaRepo;

	@Override
	public void createResourceQuotas(ResourceQuotaReqDTO resourceQuotaReqDTO) {
		resourceQuotaRepo.createResourceQuotas(resourceQuotaReqDTO.convertToResourceQuota());
	}

	@Override
	public void deleteResourceQuotas(String name, String workspace) {
		resourceQuotaRepo.deleteResourceQuotas(name, workspace);
	}

	@Override
	public ResourceQuotaResDTO getResourceQuotas(String namespace) {
		ResourceQuotaResVO resourceQuotas = resourceQuotaRepo.getResourceQuotas(namespace);
		if (resourceQuotas != null) {
			return ResourceQuotaResDTO.builder()
				.name(resourceQuotas.getName())
				.namespace(resourceQuotas.getNamespace())
				.reqCPU(resourceQuotas.getReqCPU())
				.reqGPU(resourceQuotas.getReqGPU())
				.reqMEM(resourceQuotas.getReqMEM())
				.limitCPU(resourceQuotas.getLimitCPU())
				.limitMEM(resourceQuotas.getLimitMEM())
				.limitGPU(resourceQuotas.getLimitGPU())
				.build();
		} else {
			return null;
		}
	}

	@Override
	public void updateResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq) {
		resourceQuotaRepo.updateResourceQuota(workspace, cpuReq, memReq, gpuReq);
	}

	@Override
	public List<ResourceQuotaResDTO> getResourceQuotasList() {
		return resourceQuotaRepo.getResourceQuotasList().stream().map(resourceQuotas -> ResourceQuotaResDTO.builder()
			.name(resourceQuotas.getName())
			.namespace(resourceQuotas.getNamespace())
			.reqCPU(resourceQuotas.getReqCPU())
			.reqGPU(resourceQuotas.getReqGPU())
			.reqMEM(resourceQuotas.getReqMEM())
			.limitCPU(resourceQuotas.getLimitCPU())
			.limitMEM(resourceQuotas.getLimitMEM())
			.limitGPU(resourceQuotas.getLimitGPU())
			.build()).collect(Collectors.toList());
	}
}
