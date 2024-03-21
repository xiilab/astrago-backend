package com.xiilab.servercore.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.license.dto.LicenseDTO;
import com.xiilab.servercore.license.service.LicenseService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/license")
@RequiredArgsConstructor
public class LicenseController {
	private final LicenseService licenseService;

	@GetMapping("")
	@Operation(summary = "라이센스 리스트 조회")
	public ResponseEntity<Page<LicenseDTO>> getLicenseList(Pageable pageable) {
		return new ResponseEntity<>(licenseService.getLicenseHistory(pageable), HttpStatus.OK);
	}

	@PostMapping("")
	@Operation(summary = "라이센스 추가")
	public ResponseEntity<HttpStatus> registerLicense(@RequestParam("licenseKey") String licenseKey) {
		licenseService.registerLicense(licenseKey);
		return ResponseEntity.ok().build();
	}
}
