package com.xiilab.servercore.alert.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.service.AlertManagerService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/alertManager")
@RequiredArgsConstructor
public class AlertManagerController {

	private final AlertManagerService alertManagerService;

	@PostMapping()
	@Operation(summary = "Alert Manager 등록 API")
	public ResponseEntity<AlertManagerDTO.ResponseDTO> saveAlertManager(@RequestBody AlertManagerDTO.RequestDTO requestDTO){
		return new ResponseEntity<>(alertManagerService.saveAlertManager(requestDTO), HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Alert Manager 삭제 API")
	public ResponseEntity<Void> deleteAlertManagerById(@PathVariable(name = "id") Long id){
		alertManagerService.deleteAlertManagerById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/{id}")
	@Operation(summary = "Alert Manager 단일 조회 API")
	public ResponseEntity<AlertManagerDTO.ResponseDTO> getAlertManagerById(@PathVariable(name = "id") Long id){
		return new ResponseEntity<>(alertManagerService.getAlertManagerById(id), HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Alert Manager 수정 API")
	public ResponseEntity<HttpStatus> updateMonitor(@PathVariable(name = "id") Long id,
		@RequestBody @Valid AlertManagerDTO.RequestDTO requestDTO){
		alertManagerService.updateAlertManagerById(id, requestDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "Alert Manager 전체 조회 API")
	public ResponseEntity<List<AlertManagerDTO.ResponseDTO>> getAlertManagerList(){
		return new ResponseEntity<>(alertManagerService.getAlertManagerList(), HttpStatus.OK);
	}

}
