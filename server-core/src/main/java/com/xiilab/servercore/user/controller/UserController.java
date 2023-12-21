package com.xiilab.servercore.user.controller;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.user.service.UserFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserFacadeService userFacadeService;

    @GetMapping()
    @Operation(summary = "계정 리스트 조회")
    public ResponseEntity<List<UserSummary>> getUserList(@ModelAttribute SearchCondition searchCondition) {
        return ResponseEntity.ok(userFacadeService.getUserList(searchCondition));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게정 상세 정보 조회")
    public ResponseEntity<UserInfo> getUserInfoById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(userFacadeService.getUserInfoById(id));
    }

    @GetMapping("/approval")
    @Operation(summary = "승인 대기 계정 조회")
    public ResponseEntity<List<UserSummary>> getWaitingApprovalUserList() {
        return ResponseEntity.ok(userFacadeService.getWaitingApprovalUserList());
    }

    @PatchMapping("/approval")
    @Operation(summary = "계정 승인 / 거절")
    public ResponseEntity<HttpStatus> updateUserApprovalYN(
            @RequestBody List<String> idList,
            @RequestParam(name = "approvalYN") boolean approvalYN) {
        userFacadeService.updateUserApprovalYN(idList, approvalYN);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activation")
    @Operation(summary = "계정 활성화 / 비활성화")
    public ResponseEntity<HttpStatus> updateUserActivationYN(
            @RequestBody List<String> idList,
            @RequestParam(name = "activationYN") boolean activationYN) {
        userFacadeService.updateUserActivationYN(idList, activationYN);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    @Operation(summary = "회원 가입")
    public ResponseEntity<HttpStatus> joinUser(
            @RequestParam(required = false, name = "groupId") String groupId,
            @RequestBody @Valid UserReqVO userReqVO) {
        userFacadeService.joinUser(userReqVO, groupId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reset")
    @Operation(summary = "비밀번호 초기화")
    public ResponseEntity<HttpStatus> resetPassword(@PathVariable(name = "id") String id) {
        userFacadeService.resetUserPassWord(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/updateRole")
    @Operation(summary = "계정 권한 설정")
    public ResponseEntity<HttpStatus> updateUserRole(@PathVariable(name = "id") String id, @RequestParam(name = "authType") AuthType authType) {
        userFacadeService.updateUserRole(id, authType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteUser")
    @Operation(summary = "계정 삭제")
    public ResponseEntity<HttpStatus> deleteUserById(@RequestBody List<String> idList) {
        userFacadeService.deleteUserById(idList);
        return ResponseEntity.ok().build();
    }
}
