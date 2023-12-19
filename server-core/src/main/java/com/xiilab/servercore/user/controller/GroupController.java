package com.xiilab.servercore.user.controller;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	public ResponseEntity<GroupInfoDTO> getGroupInfoById(@PathVariable(name = "id") String id) {
		return ResponseEntity.ok(groupService.getGroupInfoById(id));
	}

	@PostMapping("/")
	public ResponseEntity<HttpStatus> createAccountGroup(@RequestBody GroupReqDTO groupReqDTO) {
		groupService.createAccountGroup(groupReqDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{groupId}/members/{userId}")
	public ResponseEntity<HttpStatus> addGroupMember(@PathVariable(name = "groupId") String groupId, @PathVariable(name = "userId") String userId) {
		groupService.addGroupMember(groupId, userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable(name = "id") String id) {
		groupService.deleteGroupById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/users")
	public ResponseEntity<List<GroupUserDTO>> getGroupUsers(@PathVariable(name = "groupId") String groupId) {
		return ResponseEntity.ok(groupService.getGroupUsers(groupId));
	}

}
