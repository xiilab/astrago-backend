package com.xiilab.servercore.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.service.GroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {
	private final GroupService groupService;

	@GetMapping("/")
	public ResponseEntity<List<GroupSummaryDTO>> getGroupList() {
		return ResponseEntity.ok(groupService.getGroupList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<GroupInfoDTO> getGroupInfoById(@PathVariable String id) {
		return ResponseEntity.ok(groupService.getGroupInfoById(id));
	}

	@PostMapping("")
	public ResponseEntity<HttpStatus> createAccountGroup(@RequestBody GroupReqDTO groupReqDTO) {
		groupService.createAccountGroup(groupReqDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{groupId}/members/{userId}")
	public ResponseEntity<HttpStatus> addGroupMember(@PathVariable String groupId, @PathVariable String userId) {
		groupService.addGroupMember(groupId, userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable String id) {
		groupService.deleteGroupById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/users")
	public ResponseEntity<List<GroupUserDTO>> getGroupUsers(@PathVariable String groupId) {
		return ResponseEntity.ok(groupService.getGroupUsers(groupId));
	}

}
