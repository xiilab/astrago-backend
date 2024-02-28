package com.xiilab.servercore.alert.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.service.AlertManagerService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/alertManager")
@RequiredArgsConstructor
public class AlertManagerController {

	private final AlertManagerService alertManagerService;

	@PostMapping()
	@Operation(summary = "Alert Manager 등록 API")
	public ResponseEntity<AlertManagerDTO.ResponseDTO> saveAlertManager(@RequestBody AlertManagerDTO alertManagerDTO){
		return new ResponseEntity(alertManagerService.saveAlertManager(alertManagerDTO), HttpStatus.OK);
	}


}
