package com.xiilab.servercore.user.service;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.common.dto.SearchCondition;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {
    private final UserService userService;
    private final GroupService groupService;

    @Override
    public UserInfo joinUser(UserReqVO userReqVO, String groupId) {
        UserInfo userInfo = userService.joinUser(userReqVO);
        if (StringUtils.isNotBlank(groupId)) {
            groupService.addGroupMember(groupId, List.of(userInfo.getId()));
        }
        return userService.getUserInfoById(userInfo.getId());
    }

    @Override
    public List<UserSummary> getUserList(SearchCondition searchCondition) {
        FindDTO findDTO = FindDTO.builder()
                .option(searchCondition.getOption())
                .keyword(searchCondition.getKeyword())
                .build();
        return userService.getUserList(findDTO);
    }

    @Override
    public List<UserSummary> getWaitingApprovalUserList() {
        return userService.getWaitingApprovalUserList();
    }

    @Override
    public UserInfo getUserInfoById(String userId) {
        return userService.getUserInfoById(userId);
    }

    @Override
    public void updateUserApprovalYN(List<String> userId, boolean approvalYN) {
        userService.updateUserApprovalYN(userId, approvalYN);
    }

    @Override
    public void updateUserActivationYN(List<String> userIdList, boolean activationYN) {
        userService.updateUserActivationYN(userIdList, activationYN);
    }

    @Override
    public void resetUserPassWord(String userId) {
        userService.resetUserPassWord(userId);
    }

    @Override
    public void updateUserRole(String userId, AuthType authType) {
        userService.updateUserRole(userId, authType);
    }
}
