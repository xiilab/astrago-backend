package com.xiilab.moduleuser.service;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.repository.UserRepository;
import com.xiilab.moduleuser.vo.UserReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserInfo joinUser(UserReqVO userReqVO) {
        return userRepository.joinUser(userReqVO);
    }

    @Override
    public List<UserSummary> getUserList() {
        return userRepository.getUserList();
    }

    @Override
    public List<UserSummary> getWaitingApprovalUserList() {
        return userRepository.getUserListSearchByAttribute("approvalYN");
    }

    @Override
    public UserInfo getUserInfoById(String userId) {
        return userRepository.getUserInfoById(userId);
    }

    @Override
    public void updateUserApprovalYN(String userId, boolean approvalYN) {
        // false 일떄 해당 유저 keycloak 에서 삭제
        if (approvalYN) {
            //update attribute approval value
            userRepository.updateUserAttribute(userId, Map.of("approvalYN", String.valueOf(approvalYN)));
            //사용자 활성화 처리
            userRepository.updateUserActivationYN(userId, true);
        } else {
            //사용자 삭제 처리
            userRepository.deleteUserById(userId);
        }
    }

    @Override
    public void updateUserActivationYN(String userId, boolean activationYN) {
        userRepository.updateUserActivationYN(userId, activationYN);
    }
}
