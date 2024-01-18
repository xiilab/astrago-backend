package com.xiilab.servercore.user.controller;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.user.service.GroupFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/group")
public class GroupController {
	private final GroupFacadeService groupFacadeService;

	@GetMapping()
	@Operation(summary = "그룹 리스트 조회")
	public ResponseEntity<List<GroupSummaryDTO>> getGroupList(@ModelAttribute SearchCondition searchCondition) {
		return ResponseEntity.ok(groupFacadeService.getGroupList(searchCondition));
	}

	@GetMapping("/{id}")
	@Operation(summary = "그룹 상세 정보 조회")
	public ResponseEntity<GroupInfoDTO> getGroupInfoById(@PathVariable(name = "id") String id) {
		return ResponseEntity.ok(groupFacadeService.getGroupInfoById(id));
	}

	@PostMapping()
	@Operation(summary = "그룹 생성")
	public ResponseEntity<HttpStatus> createAccountGroup(@RequestBody GroupReqDTO groupReqDTO) {
		groupFacadeService.createAccountGroup(groupReqDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{groupId}/members")
	@Operation(summary = "그룹 멤버 추가")
	public ResponseEntity<HttpStatus> addGroupMember(@PathVariable(name = "groupId") String groupId,
		@RequestBody List<String> userIdList) {
		groupFacadeService.addGroupMember(groupId, userIdList);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "그룹 삭제")
	public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable(name = "id") String id) {
		groupFacadeService.deleteGroupById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/users")
	@Operation(summary = "그룹 멤버 조회")
	public ResponseEntity<List<GroupUserDTO>> getGroupUsers(@PathVariable(name = "groupId") String groupId) {
		return ResponseEntity.ok(groupFacadeService.getGroupUsers(groupId));
	}

}
