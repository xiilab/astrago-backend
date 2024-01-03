package com.xiilab.modulek8s.facade.provisioner;

import java.util.List;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

public interface ProvisionerModuleService {
	List<ProvisionerResDTO> findProvisioners();

	void installProvisioner(StorageType storageType);

	void unInstallProvisioner(StorageType storageType);
}
