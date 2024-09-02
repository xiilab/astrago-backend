package com.xiilab.modulek8s.storage.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8s.storage.provisioner.repository.ProvisionerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvisionerService {
	private final ProvisionerRepository provisionerRepository;

	public List<ProvisionerResDTO> findProvisioners() {
		return provisionerRepository.findProvisioners();
	}

	public void installProvisioner(StorageType storageType) {
		provisionerRepository.installProvisioner(storageType);
	}

	public void unInstallProvisioner(StorageType storageType) {
		provisionerRepository.unInstallProvisioner(storageType);
	}

	public void installDellProvisioner(String arrayId, String userName, String password, String endPoint) {
		provisionerRepository.installDellProvisioner(arrayId, userName, password, endPoint);
	}

	public void uninstallDellProvisioner() {
		provisionerRepository.uninstallDellProvisioner();
	}
}
