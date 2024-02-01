package com.xiilab.servercore.alert.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulealert.dto.AlertDTO;
import com.xiilab.modulealert.service.AlertService;
import com.xiilab.servercore.common.dto.UserInfoDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/alert")
@RequiredArgsConstructor
public class AlertController {
	private final AlertService alertService;

	@GetMapping("/{id}")
	@Operation(summary = "Alert 단일 조회")
	public ResponseEntity<AlertDTO.ResponseDTO> getAlertById(@PathVariable(name = "id") long id){
		return new ResponseEntity<>(alertService.getAlertById(id), HttpStatus.OK);
	}
	@GetMapping()
	@Operation(summary = "Alert 리스트 조회")
	public ResponseEntity<List<AlertDTO.ResponseDTO>> getAlertListByUserId(UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(alertService.getAlertListByUserId(userInfoDTO.getId()), HttpStatus.OK);
	}
	@PatchMapping("/{id}")
	@Operation(summary = "Alert 읽음 처리")
	public ResponseEntity<Void> readAlert(@PathVariable(name = "id") long id){
		alertService.readAlert(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Alert 단일 삭제")
	public ResponseEntity<Void> deleteAlertById(@PathVariable(name = "id")long id){
		alertService.deleteAlertById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
