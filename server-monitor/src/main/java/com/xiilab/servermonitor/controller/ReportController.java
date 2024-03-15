package com.xiilab.servermonitor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.servermonitor.service.ReportFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/report")
@RequiredArgsConstructor
public class ReportController {
	private final ReportFacadeService reportFacadeService;

	@GetMapping("/totalResource")
	@Operation(summary = "Cluster Monitor 전체 리소스 활용률 조회")
	public ResponseEntity<ReportDTO.ResourceUtilDTO> getClusterTotalResourceUtil(
		@RequestParam(name = "startDate") String startDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getClusterTotalResourceUtil(startDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/gpuResourceUtil")
	@Operation(summary = "Cluster Monitor GPU 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getGpuResourceUtil(
		@RequestParam(name = "startDate") String startDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getGpuResourceUtil(startDate, reportType), HttpStatus.OK);
	}

}
