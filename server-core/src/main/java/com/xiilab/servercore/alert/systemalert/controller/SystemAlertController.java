package com.xiilab.servercore.alert.systemalert.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.ModifyWorkspaceAlertMapping;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindAdminAlertMappingResDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;
import com.xiilab.servercore.alert.systemalert.service.AlertService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/core/systemAlerts")
@RequiredArgsConstructor
public class SystemAlertController {

	private final AlertService alertService;

	@PostMapping()
	@Operation(summary = "System Alert 등록")
	public ResponseEntity<Void> saveSystemAlert(@RequestBody SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO) {
		alertService.saveSystemAlert(saveSystemAlertReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "System Alert 단일 조회")
	public ResponseEntity<FindSystemAlertResDTO.SystemAlertDetail> getSystemAlertByID(
		@PathVariable(name = "id") long id) {
		return new ResponseEntity<>(alertService.getSystemAlertById(id), HttpStatus.OK);
	}

	@GetMapping()
	@Operation(summary = "알림 목록 조회")
	public ResponseEntity<FindSystemAlertResDTO.SystemAlerts> getSystemAlerts(
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		SystemAlertReqDTO.FindSearchCondition findSearchCondition
	) {
		return new ResponseEntity<>(alertService.getSystemAlerts(userInfoDTO.getId(), findSearchCondition), HttpStatus.OK);
	}

	@PatchMapping("/read/{id}")
	@Operation(summary = "System Alert 읽기여부 변경")
	public ResponseEntity<Void> readSystemAlert(@PathVariable(name = "id") long id) {
		alertService.readSystemAlert(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "System Alert 단일 삭제")
	public ResponseEntity<Void> deleteSystemAlertById(@PathVariable(name = "id") long id) {
		alertService.deleteSystemAlertById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/init")
	@Operation(summary = "관리자 알림 설정 초기값 세팅")
	public ResponseEntity<Void> initializeAdminAlertMappingSettings(@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		alertService.initializeAdminAlertMappingSettings(userInfoDTO.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/admin")
	@Operation(summary = "관리자 알림관리 설정 목록 조회")
	public ResponseEntity<FindAdminAlertMappingResDTO.AdminAlertMappings> findAdminAlertMappings(
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		return new ResponseEntity<>(alertService.findAdminAlertMappings(userInfoDTO.getId()), HttpStatus.OK);
	}

	@PutMapping("/admin")
	@Operation(summary = "관리자 알림관리 설정 목록 저장")
	public ResponseEntity<Void> saveAdminAlertMapping(@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		@RequestBody List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings) {
		alertService.saveAdminAlertMapping(userInfoDTO.getId(), saveAdminAlertMappings);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/users/ws/{workspaceResourceName}/alerts")
	@Operation(summary = "사용자 워크스페이스 알림 설정 목록 조회")
	public ResponseEntity<List<WorkspaceAlertMappingDTO>> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		List<WorkspaceAlertMappingDTO> alerts = alertService.getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(
			workspaceResourceName, userInfoDTO);
		return new ResponseEntity<>(alerts, HttpStatus.OK);
	}

	@PatchMapping("/users/ws/{workspaceResourceName}/alerts/{alertId}")
	@Operation(summary = "사용자 워크스페이스 알림 ON/OFF")
	public ResponseEntity<HttpStatus> modifyWorkspaceAlertMapping(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "alertId") String alertId,
		ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) {
		alertService.modifyWorkspaceAlertMapping(alertId, workspaceResourceName, modifyWorkspaceAlertMapping,
			userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/read")
	@Operation(summary = "알림 전체 읽기 API")
	public ResponseEntity<HttpStatus> readSystemAlerts(
		@Parameter(name = "alertRole") AlertRole alertRole,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	){
		alertService.readSystemAlerts(userInfoDTO, alertRole);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
