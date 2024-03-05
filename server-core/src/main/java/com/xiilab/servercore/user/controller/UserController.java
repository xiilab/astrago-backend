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
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.user.service.UserFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/user")
public class UserController {
	private final UserFacadeService userFacadeService;

	@GetMapping()
	@Operation(summary = "사용자 목록 조회")
	public ResponseEntity<UserDTO.PageUsersDTO> getUserList(
		PageInfo pageInfo,
		UserSearchCondition searchCondition) {
		return ResponseEntity.ok(userFacadeService.getUserList(pageInfo, searchCondition));
	}

	@GetMapping("/{id}")
	@Operation(summary = "사용자 상세 정보 조회")
	public ResponseEntity<UserDTO.UserInfo> getUserInfoById(@PathVariable(name = "id") String id) {
		return ResponseEntity.ok(userFacadeService.getUserById(id));
	}
	@PatchMapping("/{id}")
	@Operation(summary = "사용자 정보 수정")
	public ResponseEntity<HttpStatus> updateUserInfoById(@RequestBody UpdateUserDTO updateUserDTO,
		@PathVariable(name = "id") String id) {
		userFacadeService.updateUserInfoById(id, updateUserDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/approval")
	@Operation(summary = "승인 대기 사용자 목록 조회")
	public ResponseEntity<UserDTO.PageUsersDTO> getWaitingApprovalUserList(
		PageInfo pageInfo,
		UserSearchCondition searchCondition
	) {
		return ResponseEntity.ok(userFacadeService.getWaitingApprovalUserList(pageInfo, searchCondition));
	}

	@PatchMapping("/approval")
	@Operation(summary = "사용자 승인 여부 업데이트")
	public ResponseEntity<HttpStatus> updateUserApprovalYN(
		@RequestBody UserDTO.ReqUserIds reqUserIds,
		@RequestParam(name = "approvalYN") boolean approvalYN) {
		userFacadeService.updateUserApprovalYN(reqUserIds.getIds(), approvalYN);
		return ResponseEntity.ok().build();
	}

	// @PatchMapping("/{id}/activation")
	// @Operation(summary = "사용자 활성화 여부 업데이트")
	// public ResponseEntity<HttpStatus> updateUserActivationYN(
	// 	@RequestBody List<String> idList,
	// 	@RequestParam(name = "activationYN") boolean activationYN) {
	// 	userFacadeService.updateUserActivationYN(idList, activationYN);
	// 	return ResponseEntity.ok().build();
	// }
	@PatchMapping("/{id}/enable")
	@Operation(summary = "사용자 enable 업데이트")
	public ResponseEntity<HttpStatus> updateUserEnable(
		@PathVariable String id,
		@RequestParam(name = "enable") boolean enable) {
		userFacadeService.updateUserEnable(id, enable);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/join")
	@Operation(summary = "사용자 가입")
	public ResponseEntity<HttpStatus> joinUser(
		@RequestParam(required = false, name = "groupId") String groupId,
		@RequestBody @Valid UserReqVO userReqVO) {
		userFacadeService.joinUser(userReqVO, groupId);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/resetPassword")
	@Operation(summary = "비밀번호 초기화")
	public ResponseEntity<HttpStatus> resetPassword(@PathVariable(name = "id") String id) {
		userFacadeService.resetUserPassWord(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/updateRole")
	@Operation(summary = "사용자 권한 업데이트")
	public ResponseEntity<HttpStatus> updateUserRole(@PathVariable(name = "id") String id,
		@RequestParam(name = "authType") AuthType authType) {
		userFacadeService.updateUserRole(id, authType);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/deleteUser")
	@Operation(summary = "사용자 삭제")
	public ResponseEntity<HttpStatus> deleteUserById(@RequestBody List<String> idList) {
		userFacadeService.deleteUserById(idList);
		return ResponseEntity.ok().build();
	}
	@GetMapping("/search/{search}")
	@Operation(summary = "전체 검색")
	public ResponseEntity<List<SearchDTO>> getUserAndGroupBySearch(@PathVariable(name = "search") String search){
		return new ResponseEntity<>(userFacadeService.getUserAndGroupBySearch(search), HttpStatus.OK);
	}
}
