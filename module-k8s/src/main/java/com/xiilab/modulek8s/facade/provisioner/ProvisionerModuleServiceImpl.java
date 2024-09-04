package com.xiilab.modulek8s.facade.provisioner;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.StorageType;
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

	@Override
	public void unInstallProvisioner(StorageType storageType) {
		provisionerService.unInstallProvisioner(storageType);
	}

	@Override
	public void installDellProvisioner(String arrayId, String userName, String password, String endPoint) {
		provisionerService.installDellProvisioner(arrayId, userName, password, endPoint);
	}

	@Override
	public void uninstallDellProvisioner() {
		provisionerService.uninstallDellProvisioner();
	}

	@Override
	public void addProvisionerNodeLabel(String arrayId){
		provisionerService.addProvisionerNodeLabel(arrayId);
	}
}
