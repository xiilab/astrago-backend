package com.xiilab.modulek8s.storage.provisioner.repository;

import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

public interface ProvisionerRepository {

	List<ProvisionerResDTO> findProvisioners();

	void installProvisioner(StorageType storageType);

	void unInstallProvisioner(StorageType storageType);

	void installDellProvisioner(String arrayId, String userName, String password, String endPoint);

	void uninstallDellProvisioner();
}
