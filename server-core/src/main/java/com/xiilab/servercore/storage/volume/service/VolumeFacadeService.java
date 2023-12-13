package com.xiilab.servercore.storage.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.facade.StorageService;
import com.xiilab.servercore.storage.volume.dto.CreateReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolumeFacadeService {
	private final StorageService storageService;

	public void createVolume(CreateReqDTO requestDTO){
		storageService.createVolume(requestDTO.toDto());
	}

}
