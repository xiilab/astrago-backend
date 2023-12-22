package com.xiilab.servercore.provisioner.service;

import java.util.List;

import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

public interface ProvisionerFacadeService {
	List<ProvisionerResDTO> getProvisioners();
}
