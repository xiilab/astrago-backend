package com.xiilab.servercore.pin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.servercore.pin.service.PinService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/pin")
@RequiredArgsConstructor
public class PinController {
	private final PinService pinService;

	@PostMapping("")
	@Operation(summary = "workspace, workload pin 등록")
	public ResponseEntity<HttpStatus> createWorkspacePin(
		@RequestParam(value = "resourceName") String resourceName,
		@RequestParam(value = "pinType") PinType pinType,
		UserInfoDTO userInfoDTO) {
		pinService.createPin(resourceName, pinType, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("")
	@Operation(summary = "workspace, workload pin 삭제")
	public ResponseEntity<HttpStatus> deleteWorkspace(
		@RequestParam(value = "resourceName") String resourceName,
		@RequestParam(value = "pinType") PinType pinType,
		UserInfoDTO userInfoDTO) {
		pinService.deletePin(resourceName, pinType, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
