package com.xiilab.servercore.volume.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.volume.dto.CreateReqDTO;
import com.xiilab.servercore.facade.volume.service.VolumeServiceFacadeImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeServiceFacadeImpl volumeServiceFacade;

	/**
	 * 볼륨 생성
	 * @param requestDTO
	 */
	@PostMapping("/volumes")
	public void createVolume(@RequestBody CreateReqDTO requestDTO){
		volumeServiceFacade.createVolume(requestDTO);
	}



}
