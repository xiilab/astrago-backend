package com.xiilab.servercore.statistics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8sdb.statistics.repository.StatisticsRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/statistics")
@RequiredArgsConstructor
public class StatisticsController {
	private final StatisticsRepository statisticsRepository;

	@GetMapping()
	public ResponseEntity<HttpStatus> getStatistics() {

		statisticsRepository.resourceUsageDTOList();
		statisticsRepository.resourceRequestDTOList();
		statisticsRepository.getUserResourceRequestCount();
		statisticsRepository.resourceQuotaApproveCount();
		statisticsRepository.resourceQuotaRejectCount();
		statisticsRepository.getCreateWorkloadCount();
		statisticsRepository.getCreateCodeCount();
		statisticsRepository.getCreateDatasetCount();
		statisticsRepository.getCreateCredentialCount();


		return new ResponseEntity<>(HttpStatus.OK);
	}

}
