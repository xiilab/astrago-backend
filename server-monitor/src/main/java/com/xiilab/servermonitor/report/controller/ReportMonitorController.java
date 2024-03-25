package com.xiilab.servermonitor.report.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulemonitor.dto.ReportDTO;
import com.xiilab.servermonitor.report.service.ReportFacadeService;
import com.xiilab.servermonitor.report.service.ReportMonitorService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/monitor/report")
@RequiredArgsConstructor
public class ReportMonitorController {
	private final ReportFacadeService reportFacadeService;
	private final ReportMonitorService reportMonitorService;
	@GetMapping("/cluster/totalResource")
	@Operation(summary = "Cluster Monitor 전체 리소스 활용률 조회")
	public ResponseEntity<ReportDTO.ResourceUtilDTO> getClusterTotalResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getClusterTotalResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/gpuResourceUtil")
	@Operation(summary = "Cluster Monitor GPU 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getGpuResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getGpuResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/cpuResourceUtil")
	@Operation(summary = "Cluster Monitor CPU 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getCpuResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getCpuResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/memResourceUtil")
	@Operation(summary = "Cluster Monitor MEM 리소스 사용량 추이 조회")
	public ResponseEntity<List<ReportDTO.ResourceDTO>> getMemResourceUtil(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getMemResourceUtil(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/totalScore")
	@Operation(summary = "Cluster Monitor 리소스 활용 점수 추이 조회")
	public ResponseEntity<ReportDTO.ResourceDTO> getTotalResourceScore(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){
		return new ResponseEntity<>(reportFacadeService.getTotalResourceScore(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceScoreByWorkspace")
	@Operation(summary = "워크스페이스별 리소스 활용 통계 조회")
	public ResponseEntity<ReportDTO.StatisticsDTO> getResourceStatistics(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceStatistics(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceQuota")
	@Operation(summary = "리소스 신청/승인/반려 통계")
	public ResponseEntity<ReportDTO.ResourceQuotaDTO> getResourceQuota(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceQuota(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/cluster/resourceQuotaByWorkspace")
	@Operation(summary = "리소스 신청/승인/반려 통계")
	public ResponseEntity<List<ReportDTO.WorkspaceResourceQuotaDTO>> getResourceQuotaByWorkspace(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getResourceQuotaByWorkspace(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/info")
	@Operation(summary = "서버 시스템 정보 조회")
	public ResponseEntity<List<ReportDTO.SystemInfoDTO>> getSystemInfo(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType){

		return new ResponseEntity<>(reportFacadeService.getSystemInfo(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/temp")
	@Operation(summary = "시스템 이상경고 - GPU 온도 90도 이상")
	public ResponseEntity<List<ReportDTO.SystemGpuDTO>> getSystemGpuTemp(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemGpuTemp(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/cpu")
	@Operation(summary = "시스템 이상 경고 - CPU 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemCpuUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemCpuUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/mem")
	@Operation(summary = "시스템 이상 경고 - MEM 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemMemUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemMemUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/disk")
	@Operation(summary = "시스템 이상 경고 - DISK 90% 이상")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getSystemDiskUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getSystemDiskUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/nodeSystemUsage")
	@Operation(summary = "노드별 시스템 사용 현황")
	public ResponseEntity<List<ReportDTO.SystemResDTO>> getNodeSystemUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getNodeSystemUsage(endDate, reportType), HttpStatus.OK);
	}

	@GetMapping("/system/nodeGpuUsage")
	@Operation(summary = "노드별 GPU 사용 현황")
	public ResponseEntity<List<ReportDTO.SystemGpuDTO>> getNodeGpuUsage(
		@RequestParam(name = "endDate") String endDate,
		@RequestParam(name = "reportType") String reportType) {

		return new ResponseEntity<>(reportFacadeService.getNodeGpuUsage(endDate, reportType), HttpStatus.OK);
	}

	@PatchMapping("/{id}/{enable}")
	@Operation(summary = "Report 예약 발송 on/off")
	public ResponseEntity<HttpStatus> reportOnOff(@PathVariable(name = "id") Long id,
		@PathVariable(name = "enable") boolean enable){

		reportMonitorService.reportOnOff(id, enable);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
