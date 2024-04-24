package com.xiilab.servercore.alert.alertmanager.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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

import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerDTO;
import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerReceiveDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.alert.alertmanager.service.AlertManagerFacadeService;
import com.xiilab.servercore.alert.alertmanager.service.AlertManagerService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/alertManager")
@RequiredArgsConstructor
public class AlertManagerController {

	private final AlertManagerService alertManagerService;
	private final AlertManagerFacadeService alertManagerFacadeService;
	@PostMapping()
	@Operation(summary = "Alert Manager 등록 API")
	public ResponseEntity<AlertManagerDTO.ResponseDTO> saveAlertManager(@RequestBody AlertManagerDTO.RequestDTO requestDTO){
		return new ResponseEntity<>(alertManagerFacadeService.saveAlertManager(requestDTO), HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Alert Manager 삭제 API")
	public ResponseEntity<Void> deleteAlertManagerById(@PathVariable(name = "id") Long id){
		alertManagerFacadeService.deleteAlertManagerById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/{id}")
	@Operation(summary = "Alert Manager 단일 조회 API")
	public ResponseEntity<AlertManagerDTO.ResponseDTO> getAlertManagerById(@PathVariable(name = "id") Long id){
		return new ResponseEntity<>(alertManagerService.getAlertManagerById(id), HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Alert Manager 수정 API")
	public ResponseEntity<HttpStatus> updateAlertManagerById(@PathVariable(name = "id") Long id,
		@RequestBody @Valid AlertManagerDTO.RequestDTO requestDTO){
		alertManagerFacadeService.updateAlertManagerById(id, requestDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@PatchMapping("/{id}/{enable}")
	@Operation(summary = "Alert Manager 활성화 비활성화 API")
	public ResponseEntity<HttpStatus> enableAlertManagerById(@PathVariable(name = "id") Long id,
		@PathVariable(name = "enable") boolean enable){
		alertManagerFacadeService.enableAlertManagerById(id, enable);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "Alert Manager 전체 조회 API")
	public ResponseEntity<List<AlertManagerDTO.ResponseDTO>> getAlertManagerList(UserDTO.UserInfo userInfoDTO){
		return new ResponseEntity<>(alertManagerService.getAlertManagerList(userInfoDTO), HttpStatus.OK);
	}


	@PostMapping("/receive")
	@Operation(summary = "Alert Manager 알림 전달 받는 API")
	public ResponseEntity<HttpStatus> receiveAlert(@RequestBody String alertData){
		alertManagerService.receiveAlert(alertData);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/receive")
	@Operation(summary = "Alert Manager를 통하여 전달받은 알림 리스트 조회")
	public ResponseEntity<Page<AlertManagerReceiveDTO.ResponseDTO>> getAlertManagerReceiveList(
		@RequestParam(value = "categoryType", required = false) String categoryType,
		@RequestParam(value = "startDate", required = false) String startDate,
		@RequestParam(value = "endDate", required = false) String endDate,
		@RequestParam(value = "search", required = false) String search,
		Pageable pageable,
		UserDTO.UserInfo userInfoDTO){
		return new ResponseEntity<>(alertManagerService.getAlertManagerReceiveList(categoryType, startDate, endDate, search, userInfoDTO, pageable), HttpStatus.OK);
	}

	@GetMapping("/receive/{receiveId}")
	@Operation(summary = "Alert receive ID를 사용한 단일 조회")
	public ResponseEntity<AlertManagerReceiveDTO.ResponseDTO> getAlertManagerReceiveById(@PathVariable(name = "receiveId") Long receiveId){
		return new ResponseEntity<>(alertManagerService.getAlertManagerReceiveByReceiveId(receiveId), HttpStatus.OK);
	}
}
