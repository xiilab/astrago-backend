package com.xiilab.servercore.storage.volume.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.storage.volume.dto.CreateReqDTO;
import com.xiilab.servercore.storage.volume.service.VolumeFacadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeFacadeService volumeFacadeService;

	@PostMapping("/volume")
	public void createVolume(CreateReqDTO requestDTO){
		volumeFacadeService.createVolume(requestDTO);
	}
}
