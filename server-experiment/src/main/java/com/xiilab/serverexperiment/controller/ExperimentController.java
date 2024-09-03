package com.xiilab.serverexperiment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.serverexperiment.dto.ExperimentDataDTO;
import com.xiilab.serverexperiment.service.ExperimentDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/experiment")
@RequiredArgsConstructor
@Slf4j
public class ExperimentController {
	private final ExperimentDataService experimentDataService;

	@PostMapping("/train")
	public ResponseEntity<HttpStatus> saveTrainExperimentData(@RequestBody ExperimentDataDTO.TrainReq trainDataDTO) {
		experimentDataService.saveExperimentTrainData(trainDataDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/system")
	public ResponseEntity<HttpStatus> saveSystemExperimentData(@RequestBody ExperimentDataDTO.SystemReq systemDataDTO) {
		experimentDataService.saveExperimentSystemData(systemDataDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/key")
	public ResponseEntity<List<String>> getExperimentKeysByIds(@RequestParam(name = "ids") List<String> ids) {
		return new ResponseEntity<>(experimentDataService.getExperimentDataKeyByIds(ids), HttpStatus.OK);
	}

	@PostMapping("/metrics")
	public ResponseEntity<List<ExperimentDataDTO.SearchRes>> searchExperimentsGraphDataMetrics(
		@RequestBody @Validated ExperimentDataDTO.SearchReq trainDataDTO
	) {
		return new ResponseEntity<>(experimentDataService.getGraphMetrics(trainDataDTO), HttpStatus.OK);
	}

	@PostMapping("/system/metrics")
	public ResponseEntity<List<ExperimentDataDTO.SystemSearchRes>> searchSystemExperimentsGraphData(
		@RequestBody @Validated ExperimentDataDTO.SearchReq systemDataDTO
	) {
		return new ResponseEntity<>(experimentDataService.getSystemMetrics(systemDataDTO), HttpStatus.OK);
	}

	@GetMapping("/table")
	public ResponseEntity<Page<ExperimentDataDTO.TableDTO>> searchExperimentsTableData(
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "workspace") String workspace,
		@RequestParam(name = "name", required = false) String name,
		@RequestParam(name = "status", required = false) WorkloadStatus status,
		Pageable pageable
	) {
		return new ResponseEntity<>(
			experimentDataService.searchExperimentTableData(userId, workspace, name, status, pageable),
			HttpStatus.OK);
	}

	@DeleteMapping()
	public ResponseEntity<HttpStatus> deleteExperimentData(@RequestParam List<String> ids) {
		experimentDataService.deleteExperimentByIds(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
