package com.xiilab.serverbatch.controller;

import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.dto.ResourceOptimizationDTO;
import com.xiilab.serverbatch.dto.ResourceOptimizerStatus;
import com.xiilab.serverbatch.schedulerService.ResourceSchedulerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch/resource/scheduler")
public class ResourceSchedulerController {
	private final ResourceSchedulerService resourceSchedulerService;

	@GetMapping()
	public ResponseEntity<ResourceOptimizerStatus> getResourceOptimizationSchedulerStatus() throws SchedulerException {
		return new ResponseEntity<>(resourceSchedulerService.getResourceOptimizerStatus(), HttpStatus.OK);
	}

	@PostMapping("/{jobType}")
	public ResponseEntity<HttpStatus> registerResourceOptimizationScheduler(
		@PathVariable("jobType") BatchJob batchJob,
		@RequestBody ResourceOptimizationDTO resourceOptimizationDTO) throws Exception {
		resourceSchedulerService.registerResourceScheduler(resourceOptimizationDTO, batchJob);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{jobType}")
	public ResponseEntity<HttpStatus> updateResourceOptimizationValue(
		@PathVariable("jobType") BatchJob batchJob,
		@RequestBody ResourceOptimizationDTO resourceOptimizationDTO
	) throws Exception {
		resourceSchedulerService.updateResourceOptimizationScheduler(resourceOptimizationDTO, batchJob);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{jobType}")
	public ResponseEntity<HttpStatus> stopResourceOptimizationScheduler(
		@PathVariable("jobType") BatchJob batchJob
	) throws SchedulerException {
		resourceSchedulerService.stopResourceScheduler(batchJob);
		return ResponseEntity.ok().build();
	}
}
