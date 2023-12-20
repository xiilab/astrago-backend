package com.xiilab.servercore.user.controller;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.user.service.GroupFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {
	private final GroupFacadeService groupFacadeService;

	@GetMapping()
	public ResponseEntity<List<GroupSummaryDTO>> getGroupList(@ModelAttribute SearchCondition searchCondition) {
		return ResponseEntity.ok(groupFacadeService.getGroupList(searchCondition));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GroupInfoDTO> getGroupInfoById(@PathVariable(name = "id") String id) {
		return ResponseEntity.ok(groupFacadeService.getGroupInfoById(id));
	}

	@PostMapping()
	public ResponseEntity<HttpStatus> createAccountGroup(@RequestBody GroupReqDTO groupReqDTO) {
		groupFacadeService.createAccountGroup(groupReqDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{groupId}/members")
	public ResponseEntity<HttpStatus> addGroupMember(@PathVariable(name = "groupId") String groupId,
													 @RequestBody List<String> userIdList) {
		groupFacadeService.addGroupMember(groupId, userIdList);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable(name = "id") String id) {
		groupFacadeService.deleteGroupById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/users")
	public ResponseEntity<List<GroupUserDTO>> getGroupUsers(@PathVariable(name = "groupId") String groupId) {
		return ResponseEntity.ok(groupFacadeService.getGroupUsers(groupId));
	}

}
