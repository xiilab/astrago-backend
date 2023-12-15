package com.xiilab.servercore.facade.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.StorageModuleService;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolumeServiceFacadeImpl implements VolumeServiceFacade{
	private final StorageModuleService storageModuleService;

	@Override
	public void createVolume(CreateVolumeReqDTO requestDTO){
		//키클락 유저 정보 넣어줘야함

		storageModuleService.createVolume(requestDTO.toModuleDto());
	}



}
