package com.xiilab.modulek8s.storage.provisioner.repository;

import java.util.List;

import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

public interface ProvisionerRepository {

	List<ProvisionerResDTO> findProvisioners();
}
