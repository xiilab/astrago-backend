package com.xiilab.servercore.report.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/report")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	@PostMapping()
	@Operation(summary = "Report 예약 발송 등록 API")
	public ResponseEntity<ReportReservationDTO.ResponseDTO> saveReportReservation(@RequestBody ReportReservationDTO.RequestDTO requestDTO){
		return new ResponseEntity<>(reportService.saveReportReservation(requestDTO), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Report 예약 발송 삭제 API")
	public ResponseEntity<HttpStatus> deleteReportReservation(@PathVariable(name = "id") Long id){
		reportService.deleteReportReservation(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Report 예약 단일 조회 API")
	public ResponseEntity<ReportReservationDTO.ResponseDTO> getReportReservationById(@PathVariable(name = "id") Long id){
		return new ResponseEntity<>(reportService.getReportReservationById(id), HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Report 예약 수정 API")
	public ResponseEntity<HttpStatus> updateReportReservationById(@PathVariable(name = "id") Long id,
		@RequestBody @Valid ReportReservationDTO.RequestDTO requestDTO){
		reportService.updateReportReservationById(id, requestDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "Report 리스트 조회")
	public ResponseEntity<Page<ReportReservationDTO.ResponseDTO>> getReportReservationList(
		Pageable pageable,
		UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(reportService.getReportReservationList(pageable, userInfoDTO), HttpStatus.OK);
	}

	@GetMapping("/receive")
	@Operation(summary = "Report 발송 내역")
	public ResponseEntity<Page<ReportReservationDTO.ReceiveDTO>> getReportReceiveList(
		Pageable pageable,
		UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(reportService.getReportReceiveList(pageable, userInfoDTO), HttpStatus.OK);
	}

	@GetMapping("/receive/{id}")
	@Operation(summary = "Report 발송 내역 상세조회")
	public ResponseEntity<ReportReservationDTO.DetailDTO> getReportReceiveListById(
		@PathVariable(name = "id") Long id,
		UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(reportService.getReportReceiveListById(id, userInfoDTO), HttpStatus.OK);
	}
}
