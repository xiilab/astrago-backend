package com.xiilab.servercore.workload.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/workloads")
@RequiredArgsConstructor
public class WorkloadController {
	private final WorkloadModuleFacadeService workloadModuleFacadeService;

	/**
	 * 워크로드 생성 - 배치 잡
	 * @param createWorkloadJobReqDTO
	 * @return
	 */
	@PostMapping("/batch")
	@Operation(summary = "배치 잡 생성")
	public ResponseEntity<Void> createBatchJobWorkload(@RequestBody CreateWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserInfoDTO userInfoDTO) {
		createWorkloadJobReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workloadModuleFacadeService.createBatchJobWorkload(createWorkloadJobReqDTO.toModuleDTO());
		return ResponseEntity.ok().build();
	}

	/**
	 * 워크로드 생성 - 배치 잡
	 * @param createWorkloadJobReqDTO
	 * @return
	 */
	@PostMapping("/interactive")
	@Operation(summary = "인터렉티브 잡 생성")
	public ResponseEntity<Void> createInteractiveJobWorkload(
		@RequestBody CreateWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserInfoDTO userInfoDTO) {
		createWorkloadJobReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workloadModuleFacadeService.createInteractiveJobWorkload(createWorkloadJobReqDTO.toModuleDTO());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/batch")
	@Operation(summary = "워크로드 상세 조회 - Batch Job 타입")
	public ResponseEntity<ModuleBatchJobResDTO> getBatchJob(@RequestParam("workSpaceName") String workSpaceName,
		@RequestParam("workloadName") String workloadName) {
		return new ResponseEntity<>(workloadModuleFacadeService.getBatchWorkload(workSpaceName, workloadName),
			HttpStatus.OK);
	}

	@GetMapping("/interactive")
	@Operation(summary = "워크로드 상세 조회 - Interactive Job 타입")
	public ResponseEntity<ModuleInteractiveJobResDTO> getInteractiveJob(
		@RequestParam("workSpaceName") String workSpaceName,
		@RequestParam("workloadName") String workloadName) {
		return new ResponseEntity<>(workloadModuleFacadeService.getInteractiveWorkload(workSpaceName, workloadName),
			HttpStatus.OK);
	}

	@GetMapping("/jobList")
	@Operation(summary = "워크로드 리스트 조회")
	public ResponseEntity<List<ModuleWorkloadResDTO>> getWorkloadList(
		@RequestParam("workSpaceName") String workSpaceName) {
		return new ResponseEntity<>(workloadModuleFacadeService.getWorkloadList(workSpaceName), HttpStatus.OK);
	}

	@DeleteMapping("/batch")
	@Operation(summary = "워크로드 삭제 - Batch Job 타입")
	public ResponseEntity<HttpStatus> deleteBatchJob(@RequestParam("workSpaceName") String workSpaceName,
		@RequestParam("workloadName") String workloadName) {
		workloadModuleFacadeService.deleteBatchHobWorkload(workSpaceName, workloadName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/interactive")
	@Operation(summary = "워크로드 삭제 - Interactive Job 타입")
	public ResponseEntity<HttpStatus> deleteInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
		@RequestParam("workloadName") String workloadName) {
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
