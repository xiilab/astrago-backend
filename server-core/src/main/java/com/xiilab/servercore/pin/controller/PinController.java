package com.xiilab.servercore.pin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.service.PinService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/pin")
@RequiredArgsConstructor
public class PinController {
	private final PinService pinService;

	@PostMapping("/workspace")
	@Operation(summary = "workspace pin 등록")
	public ResponseEntity<HttpStatus> createWorkspacePin(@RequestParam(value = "resourceId") String resourceId,
		UserInfoDTO userInfoDTO) {
		pinService.createWorkspacePin(resourceId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/workload")
	@Operation(summary = "workload pin 등록")
	public ResponseEntity<HttpStatus> createWorkloadPin(@RequestParam(value = "resourceId") String resourceId,
		UserInfoDTO userInfoDTO) {
		pinService.createWorkloadPin(resourceId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/workspace")
	@Operation(summary = "workspace pin 삭제")
	public ResponseEntity<HttpStatus> deleteWorkspace(@RequestParam(value = "resourceId") String resourceId,
		UserInfoDTO userInfoDTO) {
		pinService.deleteWorkspacePin(resourceId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/workload")
	@Operation(summary = "workload pin 삭제")
	public ResponseEntity<HttpStatus> deleteWorkload(@RequestParam(value = "resourceId") String resourceId,
		UserInfoDTO userInfoDTO) {
		pinService.deleteWorkloadPin(resourceId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
