package com.xiilab.servercore.workload.controller;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.servercore.workload.service.WorkloadFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
public class WorkloadController {
    private final WorkloadFacadeService workloadFacadeService;

    @PostMapping("/createBatch")
    @Operation(summary = "워크로드 생성 - Batch Job 타입")
    public ResponseEntity<HttpStatus> createBatchJob(@RequestBody CreateWorkloadReqDTO createWorkloadReqDTO) {
        workloadFacadeService.createBatchJob(createWorkloadReqDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/createInteractive")
    @Operation(summary = "워크로드 생성 - Interactive Job 타입")
    public ResponseEntity<HttpStatus> createInteractiveJob(@RequestBody CreateWorkloadReqDTO createWorkloadReqDTO) {
        workloadFacadeService.createInteractiveJob(createWorkloadReqDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/batchJob")
    @Operation(summary = "워크로드 상세 조회 - Batch Job 타입")
    public ResponseEntity<JobResDTO> getBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadFacadeService.getBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @GetMapping("/interactiveJob")
    @Operation(summary = "워크로드 상세 조회 - Interactive Job 타입")
    public ResponseEntity<WorkloadResDTO> getInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
                                                            @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadFacadeService.getInteractiveJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @GetMapping("/jobList")
    @Operation(summary = "워크로드 리스트 조회")
    public ResponseEntity<List<WorkloadResDTO>> getWorkloadList(@RequestParam("workSpaceName") String workSpaceName) {
        return new ResponseEntity<>(workloadFacadeService.getWorkloadList(workSpaceName), HttpStatus.OK);
    }

    @DeleteMapping("/batchJob")
    @Operation(summary = "워크로드 삭제 - Batch Job 타입")
    public ResponseEntity<String> deleteBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadFacadeService.deleteBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @DeleteMapping("/interactiveJob")
    @Operation(summary = "워크로드 삭제 - Interactive Job 타입")
    public ResponseEntity<String> deleteInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
                                                       @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadFacadeService.deleteInteractiveJob(workSpaceName, workloadName), HttpStatus.OK);
    }
}
