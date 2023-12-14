package com.xiilab.servercore.facade.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.facade.StorageModuleService;
import com.xiilab.modulek8s.storage.facade.StorageModuleServiceImpl;
import com.xiilab.servercore.volume.dto.CreateReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolumeServiceFacadeImpl implements VolumeServiceFacade{
	private final StorageModuleService storageModuleService;

	@Override
	public void createVolume(CreateReqDTO requestDTO){
		storageModuleService.createVolume(requestDTO.toDto());
	}



}
