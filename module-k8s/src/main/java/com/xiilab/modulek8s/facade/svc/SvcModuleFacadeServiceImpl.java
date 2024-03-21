package com.xiilab.modulek8s.facade.svc;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;
import com.xiilab.modulek8s.workload.svc.service.SvcService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SvcModuleFacadeServiceImpl implements SvcModuleFacadeService{
	private final SvcService svcService;

	@Override
	public SvcResDTO.FindSvcs getServicesByResourceName(String workspaceResourceName, String workloadResourcedName) {
		return svcService.getServicesByResourceName(workspaceResourceName, workloadResourcedName);
	}
}
