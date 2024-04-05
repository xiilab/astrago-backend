package com.xiilab.servercore.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.user.service.GroupFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/group")
public class GroupController {
	private final GroupFacadeService groupFacadeService;

	@GetMapping()
	@Operation(summary = "그룹 리스트 조회")
	public ResponseEntity<List<GroupSummaryDTO>> getGroupList(
		@RequestParam(required = false, name = "searchText") String searchText) {
		return ResponseEntity.ok(groupFacadeService.getGroupList(searchText));
	}

	@GetMapping("/{groupId}")
	@Operation(summary = "그룹 상세 정보 조회")
	public ResponseEntity<GroupInfoDTO> getGroupInfoById(@PathVariable(name = "groupId") String id) {
		return ResponseEntity.ok(groupFacadeService.getGroupInfoById(id));
	}

	@PostMapping()
	@Operation(summary = "그룹 생성")
	public ResponseEntity<HttpStatus> createAccountGroup(@RequestBody GroupReqDTO groupReqDTO,
		@Parameter(hidden = true) UserInfoDTO userInfo) {
		groupFacadeService.createAccountGroup(groupReqDTO, userInfo);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{groupId}")
	@Operation(summary = "그룹 수정")
	public ResponseEntity<HttpStatus> modifyAccountGroup(
		@PathVariable(name = "groupId") String groupId,
		@RequestBody GroupReqDTO.ModifyGroupDTO groupReqDTO) {
		groupFacadeService.modifyAccountGroup(groupId, groupReqDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users")
	@Operation(summary = "그룹, 유저 검색")
	public ResponseEntity<UserDTO.SearchGroupAndUser> getUserAndGroupBySearchText(
		@RequestParam(name = "searchText") String searchText,
		@RequestParam(required = false, name = "authType") AuthType authType) {
		UserDTO.SearchGroupAndUser searchResults = groupFacadeService.getUserAndGroupBySearchText(searchText, authType);
		return new ResponseEntity<>(searchResults, HttpStatus.OK);
	}

	@PostMapping("/{groupId}/users")
	@Operation(summary = "그룹 멤버 추가")
	public ResponseEntity<HttpStatus> addGroupMember(@PathVariable(name = "groupId") String groupId,
		@RequestBody List<String> userIdList) {
		groupFacadeService.addGroupMember(groupId, userIdList);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{groupId}/users")
	@Operation(summary = "그룹 멤버 삭제")
	public ResponseEntity<HttpStatus> deleteGroupMemberByUserId(
		@PathVariable(name = "groupId") String groupId,
		@RequestBody List<String> userIdList) {
		groupFacadeService.deleteGroupMemberByUserId(groupId, userIdList);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{groupId}")
	@Operation(summary = "그룹 삭제")
	public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable(name = "groupId") String id) {
		groupFacadeService.deleteGroupById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/subGroupAndUsers")
	@Operation(summary = "그룹내 서브 그룹, 멤버 조회")
	public ResponseEntity<GroupUserDTO.SubGroupUserDto> getGroupUsers(@PathVariable(name = "groupId") String groupId,
		@RequestParam(required = false, name = "authType") AuthType authType) {
		return ResponseEntity.ok(groupFacadeService.getGroupUsers(groupId, authType));
	}

	@GetMapping("/ws/{groupName}")
	@Operation(summary = "워크스페이스 멤버 조회")
	public ResponseEntity<List<GroupUserDTO.UserDTO>> getWorkspaceMember(
		@PathVariable(name = "groupName") String groupName) {
		return ResponseEntity.ok(groupFacadeService.getWorkspaceMember(groupName));
	}

	@DeleteMapping("/ws/{groupName}")
	@Operation(summary = "워크스페이스 멤버 삭제")
	public ResponseEntity<HttpStatus> deleteWorkspaceMemberByUserId(
		@PathVariable(name = "groupName") String groupName,
		@RequestBody List<String> userIdList,
		UserInfoDTO userInfoDTO) {
		groupFacadeService.deleteWorkspaceMemberByUserId(groupName, userIdList, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/ws/{groupName}")
	@Operation(summary = "워크스페이스 멤버 추가")
	public ResponseEntity<HttpStatus> addWorkspaceMemberByUserId(
		@PathVariable(name = "groupName") String groupName,
		@RequestBody AddWorkspaceUsersDTO addWorkspaceUsersDTO,
		UserInfoDTO userInfoDTO) {
		groupFacadeService.addWorkspaceMemberByUserId(groupName, addWorkspaceUsersDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/ws/{groupName}/{search}")
	@Operation(summary = "워크스페이스 멤버 검색")
	public ResponseEntity<List<GroupUserDTO>> getWorkspaceMemberBySearch(
		@PathVariable(name = "groupName") String groupName,
		@PathVariable(name = "search") String search) {
		return new ResponseEntity<>(groupFacadeService.getWorkspaceMemberBySearch(groupName, search), HttpStatus.OK);
	}

}
