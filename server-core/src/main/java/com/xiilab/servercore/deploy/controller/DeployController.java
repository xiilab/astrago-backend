package com.xiilab.servercore.deploy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.deploy.dto.CreateDeployReqDTO;
import com.xiilab.servercore.deploy.service.DeployFacadeServiceImpl;
import com.xiilab.servercore.deploy.service.DeployService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class DeployController {
	private final DeployFacadeServiceImpl deployFacadeService;

	@PostMapping("/deploy")
	@Operation(summary = "서비스 배포 생성")
	public ResponseEntity<HttpStatus> createDeploy(
		@RequestPart(name = "createDeploy") CreateDeployReqDTO createDeployReqDTO,
		@RequestPart(name = "tritonConfigFile", required = false) MultipartFile tritonConfigFile,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	){
		deployFacadeService.createDeploy(createDeployReqDTO, tritonConfigFile, userInfoDTO);
		return null;
	}
}
