package com.xiilab.modulek8s.storage.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.storage.CSIDriver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvisionerService {
	private final ProvisionerRepository provisionerRepository;

	public List<CSIDriver> findProvisioner(){
		return provisionerRepository.findProvisioner();
	}
}
