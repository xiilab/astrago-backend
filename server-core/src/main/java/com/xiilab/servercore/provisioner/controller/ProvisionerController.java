package com.xiilab.servercore.provisioner.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;
import com.xiilab.servercore.provisioner.service.ProvisionerFacadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class ProvisionerController {
	private final ProvisionerFacadeService provisionerFacadeService;


	@GetMapping("/provisioners")
	public ResponseEntity<List<ProvisionerResDTO>> findProvisioners(){
		List<ProvisionerResDTO> provisioners = provisionerFacadeService.findProvisioners();
		return new ResponseEntity<>(provisioners, HttpStatus.OK);
	}

	@PostMapping("/provisioners")
	public ResponseEntity<Void> installProvisioner(@RequestBody InstallProvisioner installProvisioner){
		provisionerFacadeService.installProvisioner(installProvisioner);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
