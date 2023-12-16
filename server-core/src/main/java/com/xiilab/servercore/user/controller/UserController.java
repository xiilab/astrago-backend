package com.xiilab.servercore.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
	private final UserService userService;

	@GetMapping()
	public ResponseEntity<List<UserSummary>> getUserList() {
		return ResponseEntity.ok(userService.getUserList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserInfo> getUserInfoById(@PathVariable String id) {
		return ResponseEntity.ok(userService.getUserInfoById(id));
	}

	@GetMapping("/approval")
	public ResponseEntity<List<UserSummary>> getWaitingApprovalUserList() {
		return ResponseEntity.ok(userService.getWaitingApprovalUserList());
	}

	@PatchMapping("/{id}/approval")
	public ResponseEntity<HttpStatus> updateUserApprovalYN(
		@PathVariable String id,
		@RequestParam boolean approvalYN) {
		userService.updateUserApprovalYN(id, approvalYN);
		return ResponseEntity.ok(null);
	}

	@PatchMapping("/{id}/activation")
	public ResponseEntity<HttpStatus> updateUserActivationYN(
		@PathVariable String id,
		@RequestParam boolean activationYN) {
		userService.updateUserActivationYN(id, activationYN);
		return ResponseEntity.ok(null);
	}

	@PostMapping()
	public ResponseEntity<HttpStatus> joinUser() {
		userService.joinUser();
		return ResponseEntity.ok(null);
	}

}
