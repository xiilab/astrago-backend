package com.xiilab.servercore.volume.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.facade.volume.service.VolumeServiceFacade;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;
import com.xiilab.servercore.volume.service.VolumeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeService volumeService;
	private final VolumeServiceFacade volumeServiceFacade;

	/**
	 * 볼륨 생성
	 *
	 * @param requestDTO
	 * @return
	 */
	@PostMapping("/volumes")
	@Operation(summary = "create Volume")
	public ResponseEntity<Object> createVolume(@RequestBody CreateVolumeReqDTO requestDTO,
		UserInfoDTO userInfoDTO){
		volumeService.createVolume(requestDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 볼륨 전체 조회
	 *
	 * @param searchCondition
	 * @return
	 */
	@GetMapping("/volumes")
	@Operation(summary = "find Volumes")
	public ResponseEntity<List<PageVolumeResDTO>> findVolumes(@ModelAttribute SearchCondition searchCondition){
		List<PageVolumeResDTO> volumes = volumeService.findVolumes(searchCondition);
		return new ResponseEntity<>(volumes, HttpStatus.OK);
	}

	/**
	 * 볼륨 상세 조회
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@GetMapping("/volumes/{volumeMetaName}")
	public ResponseEntity<VolumeWithStorageResDTO> findVolumeByMetaName(@PathVariable("volumeMetaName") String volumeMetaName){
		VolumeWithStorageResDTO volume = volumeService.findVolumeByMetaName(volumeMetaName);
		return new ResponseEntity<>(volume, HttpStatus.OK);
	}


}
