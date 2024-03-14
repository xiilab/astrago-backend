package com.xiilab.servercore.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertService;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertSetService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {
	private final UserService userService;
	private final SystemAlertSetService alertSetService;
	private final SystemAlertService alertService;
	private final MailService mailService;
	@Value("${admin.id}")
	private String adminId;


	@Override
	public UserInfo joinUser(UserReqVO userReqVO, String groupId) {
		UserInfo userInfo = userService.joinUser(userReqVO);
		if (StringUtils.isNotBlank(groupId)) {
			userService.joinGroup(groupId, userInfo.getId());
		}else {
			userService.joinDefaultGroup(userInfo.getId());
		}

		SystemAlertSetDTO.ResponseDTO systemAlertSet = alertSetService.getSystemAlertSet();

		if(systemAlertSet.isUserSystemYN()){
			alertService.sendAlert(SystemAlertDTO.builder()
					.recipientId(adminId)
					.senderId("SYSTEM")
					.title(SystemAlertMessage.USER_CREATE.getTitle())
					.systemAlertType(SystemAlertType.USER)
					.message(
						String.format(
							SystemAlertMessage.USER_CREATE.getMessage(),
							userInfo.getLastName() + userInfo.getFirstName(),
							userInfo.getEmail()
						)
					).build());
		}
		if(systemAlertSet.isUserEmailYN()){
			UserInfo adminInfo = getUserInfoById(adminId);
			mailService.sendMail(MailDTO.builder()
					.title(SystemAlertMessage.USER_CREATE.getMailTitle())
					.receiverEmail(userInfo.getEmail())
				.build());
		}

		// return userService.getUserInfoById(userInfo.getId());
		return userService.getUserInfoById(adminId);
	}

	@Override
	public UserDTO.PageUsersDTO getUserList(PageInfo pageInfo, UserSearchCondition searchCondition) {
		return userService.getUserList(pageInfo.getPageNo(), pageInfo.getPageSize(), searchCondition);
	}

	@Override
	public UserDTO.PageUsersDTO getWaitingApprovalUserList(PageInfo pageInfo, UserSearchCondition searchCondition) {
		return userService.getWaitingApprovalUserList(pageInfo.getPageNo(), pageInfo.getPageSize(), searchCondition);
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

	@Override
	public void updateUserEnable(String id, boolean enable) {
		userService.updateUserEnable(id, enable);
	}

	@Override
	public UserDTO.UserInfo getUserById(String id) {
		return userService.getUserById(id);
	}

	@Override
	public void updateUserInfoById(String id, UpdateUserDTO updateUserDTO){
		userService.updateUserInfoById(id, updateUserDTO);
		UserInfo userInfo = userService.getUserInfoById(id);

		SystemAlertSetDTO.ResponseDTO systemAlertSet = alertSetService.getSystemAlertSet();

		if(systemAlertSet.isUserSystemYN()){
			alertService.sendAlert(SystemAlertDTO.builder()
				.recipientId(adminId)
				.senderId("SYSTEM")
				.title(SystemAlertMessage.USER_UPDATE.getTitle())
				.systemAlertType(SystemAlertType.USER)
				.message(
					String.format(
						SystemAlertMessage.USER_UPDATE.getMessage(),
						userInfo.getLastName() + userInfo.getFirstName()
					)
				).build());
		}
		if(systemAlertSet.isUserEmailYN()){
			mailService.sendMail(MailDTO.builder()
				.title(SystemAlertMessage.USER_CREATE.getMailTitle())
				.receiverEmail(userInfo.getEmail())
				.build());
		}
	}

	@Override
	public List<UserInfo> getAdminList(){
		return userService.getAdminList();
	}
}
