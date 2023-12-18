package com.xiilab.moduleuser.service;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;

import java.util.List;

public interface UserService {
    //회원가입
    UserInfo joinUser(UserReqVO userReqVO);

    //사용자 리스트 조회
    List<UserSummary> getUserList();

    //사용자 승인 신청 계정 리스트 조회
    List<UserSummary> getWaitingApprovalUserList();

    //사용자 상세 조회
    UserInfo getUserInfoById(String userId);

    //사용자 승인/거절 업데이트
    void updateUserApprovalYN(String userId, boolean approvalYN);

    //사용자 활성화/비활성화
    void updateUserActivationYN(String userId, boolean activationYN);

    void resetUserPassWord(String userId);

    void updateUserRole(String userId, AuthType authType);
}
