package com.xiilab.servercore.alert.systemalert.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/systemAlerts")
@RequiredArgsConstructor
public class SystemAlertController {

	private final SystemAlertService systemAlertService;

	@PostMapping()
	@Operation(summary = "System Alert 등록")
	public ResponseEntity<Void> saveSystemAlert(@RequestBody SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO) {
		systemAlertService.saveSystemAlert(saveSystemAlertReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "System Alert 단일 조회")
	public ResponseEntity<FindSystemAlertResDTO.SystemAlertDetail> getSystemAlertByID(
		@PathVariable(name = "id") long id) {
		return new ResponseEntity<>(systemAlertService.getSystemAlertById(id), HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "System Alert 리스트 조회")
	public ResponseEntity<FindSystemAlertResDTO.SystemAlerts> getSystemAlerts(
		@RequestParam(value = "recipientId") String recipientId, Pageable pageable) {
		return new ResponseEntity<>(systemAlertService.getSystemAlerts(recipientId, pageable), HttpStatus.OK);
	}

	@PatchMapping("/read/{id}")
	@Operation(summary = "System Alert 읽기여부 변경")
	public ResponseEntity<Void> readSystemAlert(@PathVariable(name = "id") long id) {
		systemAlertService.readSystemAlert(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "System Alert 단일 삭제")
	public ResponseEntity<Void> deleteSystemAlertById(@PathVariable(name = "id") long id) {
		systemAlertService.deleteSystemAlertById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// @GetMapping("/{id}")
	// @Operation(summary = "Alert 단일 조회")
	// public ResponseEntity<SystemAlertDTO.ResponseDTO> getAlertById(@PathVariable(name = "id") long id){
	//
	// 	return new ResponseEntity<>(systemAlertService.getAlertById(id), HttpStatus.OK);
	// }
	// @GetMapping()
	// @Operation(summary = "Alert 리스트 조회")
	// public ResponseEntity<List<SystemAlertDTO.ResponseDTO>> getAlertList(UserInfoDTO userInfoDTO){
	// 	return new ResponseEntity<>(systemAlertService.getAlertListByUserId(userInfoDTO.getId()), HttpStatus.OK);
	// }
	//
	// @PatchMapping("/{id}")
	// @Operation(summary = "Alert 읽음 처리")
	// public ResponseEntity<Void> readAlert(@PathVariable(name = "id") long id){
	// 	systemAlertService.readAlert(id);
	// 	return new ResponseEntity<>(HttpStatus.OK);
	// }
	// @DeleteMapping("/{id}")
	// @Operation(summary = "Alert 단일 삭제")
	// public ResponseEntity<Void> deleteAlertById(@PathVariable(name = "id")long id){
	// 	systemAlertService.deleteAlertById(id);
	// 	return new ResponseEntity<>(HttpStatus.OK);
	// }

}
