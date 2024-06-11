package com.xiilab.servercore.preset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.preset.dto.request.ResourcePresetReqDTO;
import com.xiilab.servercore.preset.dto.response.ResourcePresetResDTO;
import com.xiilab.servercore.preset.service.ResourcePresetService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/presets")
public class ResourcePresetController {

	private final ResourcePresetService resourcePresetService;

	@PostMapping
	@Operation(summary = "리소스 프리셋 등록")
	public ResponseEntity<HttpStatus> saveResourcePreset(
		@RequestBody ResourcePresetReqDTO.SaveResourcePreset saveResourcePreset) {
		resourcePresetService.saveResourcePreset(saveResourcePreset);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping
	@Operation(summary = "리소스 프리셋 수정")
	public ResponseEntity<HttpStatus> updateResourcePreset(
		@RequestBody ResourcePresetReqDTO.UpdateResourcePreset updateResourcePreset) {
		resourcePresetService.updateResourcePreset(updateResourcePreset);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "리소스 프리셋 상세 조회")
	public ResponseEntity<ResourcePresetResDTO.FindResourcePresetDetail> findResourcePresetById(
		@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(resourcePresetService.findResourcePresetById(id), HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "리소스 프리셋 목록 조회")
	public ResponseEntity<ResourcePresetResDTO.FindResourcePresets> findResourcePresets(
		ResourcePresetReqDTO.FindSearchCondition findSearchCondition) {
		return new ResponseEntity<>(resourcePresetService.findResourcePresets(findSearchCondition), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "리소스 프리셋 삭제")
	public ResponseEntity<HttpStatus> deleteResourcePreset(@PathVariable(name = "id") Long resourcePresetId) {
		resourcePresetService.deleteResourcePreset(resourcePresetId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
