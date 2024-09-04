package com.xiilab.modulek8s.resource_quota.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.dto.TotalResourceQuotaDTO;
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
				.reqCPU((int)resourceQuotas.getReqCPU())
				.reqGPU((int)resourceQuotas.getReqGPU())
				.reqMEM((int)resourceQuotas.getReqMEM())
				.limitCPU((int)resourceQuotas.getLimitCPU())
				.limitMEM((int)resourceQuotas.getLimitMEM())
				.limitGPU((int)resourceQuotas.getLimitGPU())
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
			.reqCPU((int)resourceQuotas.getReqCPU())
			.reqGPU((int)resourceQuotas.getReqGPU())
			.reqMEM((int)resourceQuotas.getReqMEM())
			.limitCPU((int)resourceQuotas.getLimitCPU())
			.limitMEM((int)resourceQuotas.getLimitMEM())
			.limitGPU((int)resourceQuotas.getLimitGPU())
			.useCPU((int)resourceQuotas.getUseCPU())
			.useMEM((int)resourceQuotas.getUseMEM())
			.useGPU((int)resourceQuotas.getUseGPU())
			.build()).collect(Collectors.toList());
	}

	@Override
	public TotalResourceQuotaDTO getTotalResourceQuota() {
		List<ResourceQuotaResDTO> resourceQuotasList = getResourceQuotasList();
		int reqCpuTotal = resourceQuotasList.stream().mapToInt(ResourceQuotaResDTO::getReqCPU).sum();
		int reqMemTotal = resourceQuotasList.stream().mapToInt(ResourceQuotaResDTO::getReqMEM).sum();
		int reqGpuTotal = resourceQuotasList.stream().mapToInt(ResourceQuotaResDTO::getReqGPU).sum();
		return new TotalResourceQuotaDTO(reqCpuTotal,reqMemTotal,reqGpuTotal);
	}
}
