package com.xiilab.servercore.workload.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;

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
	public ResponseEntity<Void> createInteractiveJobWorkload(@RequestBody CreateWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserInfoDTO userInfoDTO) {
		createWorkloadJobReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workloadModuleFacadeService.createInteractiveJobWorkload(createWorkloadJobReqDTO.toModuleDTO());
		return ResponseEntity.ok().build();
	}

}
