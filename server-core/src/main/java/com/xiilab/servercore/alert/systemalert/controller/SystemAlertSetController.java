package com.xiilab.servercore.alert.systemalert.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertSetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/systemAlertSet")
@RequiredArgsConstructor
public class SystemAlertSetController {

	private final SystemAlertSetService systemAlertSetService;

	@GetMapping("")
	public ResponseEntity<SystemAlertSetDTO.ResponseDTO> getSystemAlertSet(){
		return new ResponseEntity<>(systemAlertSetService.getSystemAlertSet(), HttpStatus.OK);
	}
	@PatchMapping("")
	public ResponseEntity<HttpStatus> updateSystemAlertSet(@RequestBody SystemAlertSetDTO requestDTO){
		systemAlertSetService.updateSystemAlertSet(requestDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
