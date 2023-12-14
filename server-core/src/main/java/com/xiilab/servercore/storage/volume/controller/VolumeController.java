package com.xiilab.servercore.storage.volume.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsDTO;
import com.xiilab.servercore.storage.volume.dto.CreateReqDTO;
import com.xiilab.servercore.storage.volume.service.VolumeFacadeService;

import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeFacadeService volumeFacadeService;

	/**
	 * 볼륨 생성
	 * @param requestDTO
	 */
	@PostMapping("/volumes")
	public void createVolume(@RequestBody CreateReqDTO requestDTO){
		volumeFacadeService.createVolume(requestDTO);
	}

	@GetMapping("/volumes/{volumeMetaName}/workloads")
	public ResponseEntity<VolumeWithWorkloadsDTO> findVolumeWithWorkloadsByMetaName(@PathVariable("volumeMetaName") String volumeMetaName){
		VolumeWithWorkloadsDTO result = volumeFacadeService.findVolumeWithWorkloadsByMetaName(volumeMetaName);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
