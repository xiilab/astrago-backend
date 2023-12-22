package com.xiilab.servercore.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.StorageModuleService;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvisionerFacadeServiceImpl implements ProvisionerFacadeService{
	private final StorageModuleService storageModuleService;

	@Override
	public List<ProvisionerResDTO> getProvisioners() {
		return storageModuleService.getProvisioners();
	}
}
