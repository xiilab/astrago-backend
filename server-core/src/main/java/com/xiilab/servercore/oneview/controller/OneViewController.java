package com.xiilab.servercore.oneview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulecommon.enums.OneViewAccountConnectionStatus;
import com.xiilab.servercore.oneview.dto.OneViewReqDTO;
import com.xiilab.servercore.oneview.dto.OneViewResDTO;
import com.xiilab.servercore.oneview.service.OneViewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "OneViewController", description = "원뷰 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/oneview")
public class OneViewController {
	private final OneViewService oneViewService;

	@GetMapping
	@Operation(summary = "원뷰 정보 조회")
	public ResponseEntity<OneViewResDTO.FindOneViewSetting> findOneViewSetting() {
		return ResponseEntity.ok().body(oneViewService.getOneViewSetting());
	}

	@PostMapping
	@Operation(summary = "원뷰 정보 설정")
	public ResponseEntity<Void> saveOneViewSetting(
		@Valid @RequestBody OneViewReqDTO.SaveOneViewSetting saveOneViewSettingDTO
	) {
		oneViewService.saveOneViewSetting(saveOneViewSettingDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/status")
	@Operation(summary = "원뷰 계졍 연동 상태 조회")
	public ResponseEntity<OneViewAccountConnectionStatus> getOneViewAccountConnectionStatus() {
		return ResponseEntity.ok().body(oneViewService.getOneViewAccountConnectionStatus());
	}

	@GetMapping("/token")
	@Operation(summary = "원뷰 토큰 발급")
	public ResponseEntity<String> getOneViewSessionToken() {
		return ResponseEntity.ok().body(oneViewService.getOneViewSessionToken());
	}
}
