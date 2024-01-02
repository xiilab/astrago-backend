package com.xiilab.servercore.provisioner.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.servercore.provisioner.service.ProvisionerFacadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class ProvisionerController {
	private final ProvisionerFacadeService provisionerFacadeService;


	@GetMapping("/provisioners")
	public ResponseEntity<List<ProvisionerResDTO>> getProvisioners(){
		List<ProvisionerResDTO> provisioners = provisionerFacadeService.getProvisioners();
		return new ResponseEntity<>(provisioners, HttpStatus.OK);
	}
}
