package com.xiilab.serverexperiment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.serverexperiment.dto.TrainDataDTO;
import com.xiilab.serverexperiment.dto.TrainDataSearchDTO;
import com.xiilab.serverexperiment.service.TrainDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/core/experiment")
@RequiredArgsConstructor
@Slf4j
public class TrainDataController {
	private final TrainDataService trainDataService;

	@PostMapping
	public ResponseEntity<HttpStatus> saveTrainData(@RequestBody TrainDataDTO.Req trainDataDTO) {
		log.info(trainDataDTO.getUuid());
		trainDataService.saveTrainData(trainDataDTO);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/key")
	public ResponseEntity<List<String>> getTrainDataKeysByIds(@RequestParam(name = "ids") List<String> ids) {
		return new ResponseEntity<>(trainDataService.getTrainDataKeyByIds(ids), HttpStatus.OK);
	}

	@GetMapping("/metrics")
	public ResponseEntity<List<TrainDataSearchDTO>> searchTrainData(
		@RequestBody TrainDataDTO.SearchReq searchReq
	) {
		return new ResponseEntity<>(
			trainDataService.searchTrainData(searchReq.getExperiments(), searchReq.getMetrics()), HttpStatus.OK);
	}
}
