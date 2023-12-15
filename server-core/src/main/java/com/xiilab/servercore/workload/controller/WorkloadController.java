package com.xiilab.servercore.workload.controller;

import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;
import com.xiilab.servercore.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workload")
@RequiredArgsConstructor
public class WorkloadController {
    private final WorkloadService workloadService;

    //interactive job 조회, 수정

    @GetMapping("/batchJob")
    public ResponseEntity<JobResDTO> getBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadService.getBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

//    @GetMapping("/interactiveJob")
//    public void getInteractiveJob(String workSpaceName, String workloadName) {
//        workloadRepo.deleteInteractiveJobWorkload(workSpaceName,workloadName);
//    }

    @GetMapping("/jobList")
    public ResponseEntity<List<WorkloadRes>> getWorkloadList(@RequestParam("workSpaceName") String workSpaceName) {
        return new ResponseEntity<>(workloadService.getWorkloadList(workSpaceName),HttpStatus.OK);
    }

    @DeleteMapping("/batchJob")
    public ResponseEntity<String> deleteBatchJob(@RequestParam("workSpaceName") String workSpaceName,
                                                 @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadService.deleteBatchJob(workSpaceName, workloadName), HttpStatus.OK);
    }

    @DeleteMapping("/interactiveJob")
    public ResponseEntity<String> deleteInteractiveJob(@RequestParam("workSpaceName") String workSpaceName,
                                                       @RequestParam("workloadName") String workloadName) {
        return new ResponseEntity<>(workloadService.deleteInteractiveJob(workSpaceName, workloadName), HttpStatus.OK);
    }
}
