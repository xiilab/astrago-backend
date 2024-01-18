package com.xiilab.moduleuser.service;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.AuthType;
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
	public List<UserSummary> getUserList(FindDTO findDTO) {
		return userRepository.getUserList(findDTO);
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
	public void updateUserApprovalYN(List<String> userIdList, boolean approvalYN) {
        // false 일떄 해당 유저 keycloak 에서 삭제
        if (approvalYN) {
            //update attribute approval value
			userRepository.updateUserAttribute(userIdList, Map.of("approvalYN", String.valueOf(approvalYN)));
            //사용자 활성화 처리
			userRepository.updateUserActivationYN(userIdList, true);
        } else {
            //사용자 삭제 처리
			userRepository.deleteUserById(userIdList);
        }
    }

    @Override
	public void updateUserActivationYN(List<String> userIdList, boolean activationYN) {
		userRepository.updateUserActivationYN(userIdList, activationYN);
    }

    @Override
    public void resetUserPassWord(String userId) {
        userRepository.resetUserPassWord(userId);
    }

    @Override
    public void updateUserRole(String userId, AuthType authType) {
        userRepository.updateUserRole(userId,authType);
    }

	@Override
	public void joinGroup(String groupId, String userId) {
		userRepository.joinGroup(groupId, userId);
	}

	@Override
	public void deleteUserById(List<String> userId) {
		userRepository.deleteUserById(userId);
	}
}
