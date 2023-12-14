package com.xiilab.modulek8s.storage.provisioner.service;

import java.util.List;

import io.fabric8.kubernetes.api.model.storage.CSIDriver;

public interface ProvisionerRepository {
	List<CSIDriver> findProvisioner();

}
