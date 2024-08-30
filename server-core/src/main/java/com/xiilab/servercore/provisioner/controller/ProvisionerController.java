package com.xiilab.servercore.provisioner.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;
import com.xiilab.servercore.provisioner.service.ProvisionerFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class ProvisionerController {
	private final ProvisionerFacadeService provisionerFacadeService;

	@GetMapping("/provisioners/installed")
	@Operation(summary = "프로비저너 조회")
	public ResponseEntity<List<ProvisionerResDTO>> findProvisioners() {
		List<ProvisionerResDTO> provisioners = provisionerFacadeService.findProvisioners();
		return new ResponseEntity<>(provisioners, HttpStatus.OK);
	}

	@PostMapping("/provisioners")
	@Operation(summary = "프로비저너 설치")
	public ResponseEntity<Void> installProvisioner(@RequestBody InstallProvisioner installProvisioner) {
		provisionerFacadeService.installProvisioner(installProvisioner);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/provisioners/types/{type}")
	@Operation(summary = "프로비저너 제거")
	public ResponseEntity<Void> unInstallProvisioner(@PathVariable("type") StorageType storageType) {
		provisionerFacadeService.unInstallProvisioner(storageType);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/plugin")
	@Operation(summary = "플러그인 목록 조회")
	public ResponseEntity<List<PluginDTO.ResponseDTO>> getInstallableProvisioners() {
		return new ResponseEntity<>(provisionerFacadeService.getPluginList(), HttpStatus.OK);
	}

	@PostMapping("/plugin/install/{type}")
	@Operation(summary = "플러그인 설치 ")
	public ResponseEntity<HttpStatus> installPlugin(
		@PathVariable(name = "type") String type,
		@RequestBody(required = false) PluginDTO.DellUnityDTO dellUnityDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		provisionerFacadeService.installPlugin(type, dellUnityDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@DeleteMapping("/plugin/uninstall/{type}")
	@Operation(summary = "플러그인 삭제 ")
	public ResponseEntity<HttpStatus> unInstallPlugin(
		@PathVariable(name = "type") String type,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		provisionerFacadeService.uninstallPlugin(type, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
