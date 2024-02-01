package com.xiilab.servercore.workload.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;
import com.xiilab.servercore.workload.enumeration.WorkloadSortCondition;
import com.xiilab.servercore.workload.service.WorkloadFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/workloads")
@RequiredArgsConstructor
public class WorkloadController {
	private final WorkloadFacadeService workloadFacadeService;

	@PostMapping("/{type}")
	@Operation(summary = "워크로드 생성")
	public ResponseEntity<HttpStatus> createWorkload(
		@RequestBody CreateWorkloadJobReqDTO createWorkloadJobReqDTO,
		@PathVariable(value = "type") WorkloadType workloadType,
		UserInfoDTO userInfoDTO) {
		workloadFacadeService.createWorkload(createWorkloadJobReqDTO, workloadType, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{type}")
	@Operation(summary = "워크로드 상세 조회")
	public ResponseEntity<ModuleWorkloadResDTO> getWorkloadInfo(
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("resourceName") String resourceName,
		@PathVariable(value = "type") WorkloadType workloadType) {
		return new ResponseEntity<>(workloadFacadeService.getWorkloadInfoByResourceName(workspaceResourceName, resourceName, workloadType), HttpStatus.OK);
	}

	@GetMapping("/jobList")
	@Operation(summary = "워크로드 리스트 조회")
	public ResponseEntity<PageDTO<ModuleWorkloadResDTO>> getWorkloadList(
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "searchName", required = false) String searchName,
		@RequestParam(value = "workloadStatus", required = false) WorkloadStatus workloadStatus,
		@RequestParam(value = "workloadSortCondition", required = false) WorkloadSortCondition workloadSortCondition,
		@RequestParam(value = "pageNum") int pageNum,
		UserInfoDTO userInfoDTO
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getWorkloadListByCondition(workloadType, workspaceName, searchName, workloadStatus,
				workloadSortCondition, pageNum, userInfoDTO), HttpStatus.OK);
	}

	@DeleteMapping("/{type}")
	@Operation(summary = "워크로드 삭제 api")
	public ResponseEntity<HttpStatus> deleteWorkload(
		@PathVariable(value = "type") WorkloadType workloadType,
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("resourceName") String resourceName,
		UserInfoDTO userInfoDTO
	) throws IOException {
		workloadFacadeService.deleteWorkload(workspaceResourceName, resourceName, workloadType, userInfoDTO);
		return ResponseEntity.ok().build();
	}
}
