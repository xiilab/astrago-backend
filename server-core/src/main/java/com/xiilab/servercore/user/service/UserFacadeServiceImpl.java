package com.xiilab.servercore.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.common.dto.SearchCondition;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {
	private final UserService userService;

	@Override
	public UserInfo joinUser(UserReqVO userReqVO, String groupId) {
		UserInfo userInfo = userService.joinUser(userReqVO);
		if (StringUtils.isNotBlank(groupId)) {
			userService.joinGroup(groupId, userInfo.getId());
		}else {
			userService.joinDefaultGroup(userInfo.getId());
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

	@Override
	public void deleteUserById(List<String> userIdList) {
		userService.deleteUserById(userIdList);
	}
	@Override
	public List<SearchDTO> getUserAndGroupBySearch(String search){
		return userService.getUserAndGroupBySearch(search);
	}
}
