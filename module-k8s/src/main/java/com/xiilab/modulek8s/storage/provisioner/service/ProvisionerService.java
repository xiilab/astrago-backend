package com.xiilab.modulek8s.storage.provisioner.service;

import io.fabric8.kubernetes.api.model.storage.CSIDriver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvisionerService {
	private final ProvisionerRepository provisionerRepository;

	public List<CSIDriver> findProvisioner(){
		return provisionerRepository.findProvisioner();
	}
}
