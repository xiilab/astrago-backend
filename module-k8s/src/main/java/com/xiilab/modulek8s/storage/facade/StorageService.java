package com.xiilab.modulek8s.storage.facade;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {
	private final ProvisionerService provisionerService;



}
