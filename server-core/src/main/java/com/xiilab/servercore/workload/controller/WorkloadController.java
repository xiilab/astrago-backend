package com.xiilab.servercore.workload.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
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
	public ResponseEntity<Void> createInteractiveJobWorkload(@RequestBody CreateWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserInfoDTO userInfoDTO) {
		createWorkloadJobReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workloadModuleFacadeService.createInteractiveJobWorkload(createWorkloadJobReqDTO.toModuleDTO());
		return ResponseEntity.ok().build();
	}


    @GetMapping("/batch")
    @Operation(summary = "워크로드 상세 조회 - Batch Job 타입")
    public ResponseEntity<JobResDTO> getBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadModuleFacadeService.getBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @GetMapping("/interactive")
    @Operation(summary = "워크로드 상세 조회 - Interactive Job 타입")
    public ResponseEntity<WorkloadResDTO> getInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
                                                            @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadModuleFacadeService.getInteractiveJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @GetMapping("/jobList")
    @Operation(summary = "워크로드 리스트 조회")
    public ResponseEntity<List<WorkloadResDTO>> getWorkloadList(@RequestParam("workSpaceName") String workSpaceName) {
        return new ResponseEntity<>(workloadModuleFacadeService.getWorkloadList(workSpaceName), HttpStatus.OK);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "워크로드 삭제 - Batch Job 타입")
    public ResponseEntity<String> deleteBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadModuleFacadeService.deleteBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @DeleteMapping("/interactive")
    @Operation(summary = "워크로드 삭제 - Interactive Job 타입")
    public ResponseEntity<String> deleteInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
                                                       @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadModuleFacadeService.deleteInteractiveJob(workSpaceName, workloadName), HttpStatus.OK);
    }

}
