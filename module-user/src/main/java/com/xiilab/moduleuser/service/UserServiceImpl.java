package com.xiilab.moduleuser.service;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.event.UserAlertEvent;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.repository.UserRepository;
import com.xiilab.moduleuser.vo.UserReqVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;
	@Override
	public UserInfo joinUser(UserReqVO userReqVO) {
		return userRepository.joinUser(userReqVO);
	}

	@Override
	public UserDTO.PageUsersDTO getUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition) {
		return userRepository.getUserList(pageNo, pageSize, searchCondition);
	}

	@Override
	public UserDTO.PageUsersDTO getWaitingApprovalUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition) {
		return userRepository.getWaitingApprovalUserList(pageNo, pageSize, searchCondition);
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
		userRepository.updateUserRole(userId, authType);
	}

	@Override
	public void joinGroup(String groupId, String userId) {
		userRepository.joinGroup(groupId, userId);
	}
	@Override
	public void joinDefaultGroup(String userId) {
		userRepository.joinDefaultGroup(userId);
	}

	@Override
	public void deleteUserById(List<String> userId) {
		userRepository.deleteUserById(userId);
	}
	@Override
	public List<SearchDTO> getUserAndGroupBySearch(String search){
		return userRepository.getUserAndGroupBySearch(search);
	}
	@Override
	public void updateUserInfoById(String id, UpdateUserDTO updateUserDTO){
		userRepository.updateUserInfoById(id, updateUserDTO);
	}

	@Override
	public void updateUserEnable(String id, boolean enable) {
		UserAlertEvent userAlertEvent = null;
		userRepository.updateUserEnable(id, enable);
		UserInfo userInfo = userRepository.getUserInfoById(id);
		//알림 발송 해야함
		//true 활성화
		if(enable){
			AlertMessage userEnabled = AlertMessage.USER_ENABLED;
			String emailTitle = String.format(userEnabled.getMailTitle());
			String title = userEnabled.getTitle();
			String message = String.format(userEnabled.getMessage(), userInfo.getLastName() + userInfo.getFirstName());
			userAlertEvent = new UserAlertEvent(null, emailTitle, title, message, id);
		}else{//false 비활성화
			String emailTitle = String.format(AlertMessage.USER_DISABLED.getMailTitle());
			String title = AlertMessage.USER_DISABLED.getTitle();
			String message = String.format(AlertMessage.USER_DISABLED.getMessage(), userInfo.getLastName() + userInfo.getFirstName());
			userAlertEvent = new UserAlertEvent(null, emailTitle, title, message, id);
		}

		eventPublisher.publishEvent(userAlertEvent);
	}

	@Override
	public UserDTO.UserInfo getUserById(String id) {
		return userRepository.getUserById(id);
	}
	@Override
	public List<UserInfo> getAdminList(){
		return userRepository.getAdminList();
	}

	@Override
	public void joinAdmin(UserReqVO userReqVO) {
		userRepository.joinAdmin(userReqVO);
	}

}
