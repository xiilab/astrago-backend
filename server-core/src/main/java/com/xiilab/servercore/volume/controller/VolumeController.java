package com.xiilab.servercore.volume.controller;

import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;
import com.xiilab.servercore.volume.dto.ModifyVolumeReqDTO;
import com.xiilab.servercore.volume.service.VolumeFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/core/volumes")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeFacadeService volumeFacadeService;

	/**
	 * 볼륨 생성
	 *
	 * @param requestDTO
	 * @return
	 */
	@PostMapping("")
	@Operation(summary = "create Volume")
	public ResponseEntity<Object> createVolume(@RequestBody CreateVolumeReqDTO requestDTO,
		UserInfoDTO userInfoDTO){
		volumeFacadeService.createVolume(requestDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 볼륨 전체 조회
	 *
	 * @param searchCondition
	 * @return
	 */
	@GetMapping("")
	@Operation(summary = "find Volumes")
	public ResponseEntity<List<PageVolumeResDTO>> findVolumes(@ModelAttribute SearchCondition searchCondition){
		List<PageVolumeResDTO> volumes = volumeFacadeService.findVolumes(searchCondition);
		return new ResponseEntity<>(volumes, HttpStatus.OK);
	}

	/**
	 * 볼륨 상세 조회
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@GetMapping("/{volumeMetaName}")
	@Operation(summary = "find Volume")
	public ResponseEntity<VolumeWithStorageResDTO> findVolumeByMetaName(@PathVariable("volumeMetaName") String volumeMetaName){
		VolumeWithStorageResDTO volume = volumeFacadeService.findVolumeByMetaName(volumeMetaName);
		return new ResponseEntity<>(volume, HttpStatus.OK);
	}

	/**
	 * 볼륨 삭제
	 * @param volumeMetaName
	 * @return
	 */
	@DeleteMapping("/{volumeMetaName}")
	@Operation(summary = "delete Volume")
	public ResponseEntity<Object> deleteVolumeByMetaName(@PathVariable("volumeMetaName") String volumeMetaName){
		volumeFacadeService.deleteVolumeByMetaName(volumeMetaName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 볼륨 수정
	 *
	 * @param volumeMetaName
	 * @param modifyVolumeReqDTO
	 * @return
	 */
	@PutMapping("/{volumeMetaName}")
	@Operation(summary = "modify Volume")
	public ResponseEntity<Object> modifyVolume(@PathVariable("volumeMetaName") String volumeMetaName,
		@RequestBody ModifyVolumeReqDTO modifyVolumeReqDTO){
		volumeFacadeService.modifyVolume(modifyVolumeReqDTO, volumeMetaName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
