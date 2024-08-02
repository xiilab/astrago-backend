package com.xiilab.serverexperiment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping
	public ResponseEntity<HttpStatus> saveExperimentData(@RequestBody ExperimentDataDTO.Req trainDataDTO) {
		experimentDataService.saveExperimentData(trainDataDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/key")
	public ResponseEntity<List<String>> getExperimentKeysByIds(@RequestParam(name = "ids") List<String> ids) {
		return new ResponseEntity<>(experimentDataService.getExperimentDataKeyByIds(ids), HttpStatus.OK);
	}

	@GetMapping("/{id}/graph")
	public ResponseEntity<List<ExperimentDataDTO.SearchRes>> searchExperimentsGraphData(
		@PathVariable(name = "id") Long id,
		@RequestParam(name = "experiments") List<String> experiments
	) {
		return new ResponseEntity<>(
			experimentDataService.searchExperimentsGraphData(id, experiments), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<ExperimentDataDTO.Res>> searchExperimentsTableData(
		@RequestBody ExperimentDataDTO.SearchReq searchReq
	) {
		return new ResponseEntity<>(
			experimentDataService.searchExperimentTableData(searchReq.getExperiments(), searchReq.getMetrics()),
			HttpStatus.OK);
	}
}
