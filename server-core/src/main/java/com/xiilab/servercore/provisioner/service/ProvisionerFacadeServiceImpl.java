package com.xiilab.servercore.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.provisioner.ProvisionerModuleService;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvisionerFacadeServiceImpl implements ProvisionerFacadeService{
	private final ProvisionerModuleService provisionerModuleService;

	@Override
	public List<ProvisionerResDTO> findProvisioners() {
		return provisionerModuleService.findProvisioners();
	}
}
