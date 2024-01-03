package com.xiilab.modulek8s.facade.provisioner;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProvisionerModuleServiceImpl implements ProvisionerModuleService {
	private final ProvisionerService provisionerService;

	@Override
	public List<ProvisionerResDTO> findProvisioners() {
		return provisionerService.findProvisioners();
	}

	@Override
	public void installProvisioner(StorageType storageType) {
		provisionerService.installProvisioner(storageType);
	}
}
