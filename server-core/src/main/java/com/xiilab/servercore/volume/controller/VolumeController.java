package com.xiilab.servercore.volume.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;
import com.xiilab.servercore.volume.dto.ModifyVolumeReqDTO;
import com.xiilab.servercore.volume.service.VolumeFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "VolumeController", description = "볼륨 테스트용 API")
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
	@Operation(summary = "볼륨 생성")
	public ResponseEntity<HttpStatus> createVolume(@RequestBody CreateVolumeReqDTO requestDTO,
		UserDTO.UserInfo userInfoDTO) {
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
	@Operation(summary = "볼륨 목록 조회")
	public ResponseEntity<List<PageVolumeResDTO>> findVolumes(@ModelAttribute SearchCondition searchCondition) {
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
	@Operation(summary = "볼륨 메타명으로 볼륨 조회")
	public ResponseEntity<VolumeWithStorageResDTO> findVolumeByMetaName(
		@PathVariable("volumeMetaName") String volumeMetaName) {
		VolumeWithStorageResDTO volume = volumeFacadeService.findVolumeByMetaName(volumeMetaName);
		return new ResponseEntity<>(volume, HttpStatus.OK);
	}

	/**
	 * 볼륨 삭제
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@DeleteMapping("/{volumeMetaName}")
	@Operation(summary = "볼륨 메타명으로 볼륨 삭제")
	public ResponseEntity<Object> deleteVolumeByMetaName(@PathVariable("volumeMetaName") String volumeMetaName) {
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
	@Operation(summary = "볼륨 메타명으로 볼륨 수정")
	public ResponseEntity<Object> modifyVolume(@PathVariable("volumeMetaName") String volumeMetaName,
		@RequestBody ModifyVolumeReqDTO modifyVolumeReqDTO) {
		volumeFacadeService.modifyVolume(modifyVolumeReqDTO, volumeMetaName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
