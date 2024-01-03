package com.xiilab.servercore.provisioner.service;

import java.util.List;

import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;

public interface ProvisionerFacadeService {
	List<ProvisionerResDTO> findProvisioners();

	void installProvisioner(InstallProvisioner installProvisioner);
}
