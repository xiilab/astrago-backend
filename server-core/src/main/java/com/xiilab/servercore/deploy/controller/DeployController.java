package com.xiilab.servercore.deploy.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.deploy.dto.DeploySearchCondition;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.deploy.dto.CreateDeployReqDTO;
import com.xiilab.servercore.deploy.dto.ResDeploys;
import com.xiilab.servercore.deploy.dto.ResReplica;
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

	@PostMapping("/deploys")
	@Operation(summary = "서비스 배포 생성")
	public ResponseEntity<HttpStatus> createDeploy(
		@RequestPart(name = "createDeploy") CreateDeployReqDTO createDeployReqDTO,
		@RequestPart(name = "tritonConfigFile", required = false) MultipartFile tritonConfigFile,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	){
		deployFacadeService.createDeploy(createDeployReqDTO, tritonConfigFile, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/deploys")
	@Operation(summary = "서비스 리스트 조회")
	public ResponseEntity<ResDeploys> getDeploys(
		@RequestParam(value = "workspaceResourceName") String workspaceResourceName,
		DeploySearchCondition deploySearchCondition
		){
		ResDeploys deploys = deployFacadeService.getDeploys(workspaceResourceName, deploySearchCondition);
		return new ResponseEntity<>(deploys, HttpStatus.OK);
	}
	@GetMapping("/deploys/replicas")
	@Operation(summary = "replica 리스트 조회")
	public ResponseEntity<List<ResReplica>> getReplicasByDeployResourceName(
		@RequestParam(value = "workspaceResourceName") String workspaceResourceName,
		@RequestParam(value = "deployResourceName") String deployResourceName
	){
		List<ResReplica> replicas = deployFacadeService.getReplicasByDeployResourceName(
			workspaceResourceName, deployResourceName);
		return new ResponseEntity<>(replicas, HttpStatus.OK);
	}
	@DeleteMapping("/deploys/{deployResourceName}")
	@Operation(summary = "서비스 종료 api")
	public ResponseEntity<HttpStatus> stopDeploy(
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "deployResourceName") String deployResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) throws IOException {
		deployFacadeService.stopDeploy(workspaceResourceName, deployResourceName, userInfoDTO);
		return ResponseEntity.ok().build();
	}


}
