package com.xiilab.servercore.user.controller;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserSummary>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getUserInfoById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(userService.getUserInfoById(id));
    }

    @GetMapping("/approval")
    public ResponseEntity<List<UserSummary>> getWaitingApprovalUserList() {
        return ResponseEntity.ok(userService.getWaitingApprovalUserList());
    }

    @PatchMapping("/{id}/approval")
    public ResponseEntity<HttpStatus> updateUserApprovalYN(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "approvalYN") boolean approvalYN) {
        userService.updateUserApprovalYN(id, approvalYN);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activation")
    public ResponseEntity<HttpStatus> updateUserActivationYN(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "activationYN") boolean activationYN) {
        userService.updateUserActivationYN(id, activationYN);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    public ResponseEntity<HttpStatus> joinUser(@RequestBody UserReqVO userReqVO) {
        userService.joinUser(userReqVO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reset")
    public ResponseEntity<HttpStatus> resetPassword(@PathVariable(name = "id") String id) {
        userService.resetUserPassWord(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/updateRole")
    public ResponseEntity<HttpStatus> updateUserRole(@PathVariable(name = "id") String id, @RequestParam(name = "authType") AuthType authType) {
        userService.updateUserRole(id,authType);
        return ResponseEntity.ok().build();
    }
}
