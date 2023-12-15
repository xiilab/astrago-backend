package com.xiilab.moduleuser.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	@Override
	public UserInfo joinUser() {
		return null;
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
		userRepository.updateUserAttribute(userId, Map.of("approvalYN", String.valueOf(approvalYN)));
	}

	@Override
	public void updateUserActivationYN(String userId, boolean activationYN) {
		userRepository.updateUserActivationYN(userId,activationYN);
	}
}
