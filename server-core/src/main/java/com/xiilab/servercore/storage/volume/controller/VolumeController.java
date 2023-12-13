package com.xiilab.servercore.storage.volume.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.facade.StorageService;
import com.xiilab.modulek8s.storage.facade.dto.StorageReqDTO;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VolumeController {
	private final StorageService storageService;

	@PostMapping("/volume")
	public void createVolume(StorageReqDTO requestDTO){
		storageService.createVolume(requestDTO);
	}
}
