package com.xiilab.servercore.alert.systemalert.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/system")
@RequiredArgsConstructor
public class SystemAlertController {

	private final SystemAlertService systemAlertService;

	@GetMapping("/{id}")
	@Operation(summary = "Alert 단일 조회")
	public ResponseEntity<SystemAlertDTO.ResponseDTO> getAlertById(@PathVariable(name = "id") long id){

		return new ResponseEntity<>(systemAlertService.getAlertById(id), HttpStatus.OK);
	}
	@GetMapping()
	@Operation(summary = "Alert 리스트 조회")
	public ResponseEntity<List<SystemAlertDTO.ResponseDTO>> getAlertListByUserId(UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(systemAlertService.getAlertListByUserId(userInfoDTO.getId()), HttpStatus.OK);
	}
	@PatchMapping("/{id}")
	@Operation(summary = "Alert 읽음 처리")
	public ResponseEntity<Void> readAlert(@PathVariable(name = "id") long id){
		systemAlertService.readAlert(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Alert 단일 삭제")
	public ResponseEntity<Void> deleteAlertById(@PathVariable(name = "id")long id){
		systemAlertService.deleteAlertById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}