package com.xiilab.servercore.user.service;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.alert.event.UserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.enums.WorkspaceRole;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.alert.systemalert.service.AlertService;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertSetService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {
	private final UserService userService;
	private final SystemAlertSetService alertSetService;
	private final AlertService alertService;
	private final MailService mailService;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void joinUser(UserReqVO userReqVO, String groupId) {
		UserDTO.UserInfo userInfo = userService.joinUser(userReqVO);
		if (StringUtils.isNotBlank(groupId)) {
			userService.joinDefaultGroup(userInfo.getId());
			userService.joinGroup(groupId, userInfo.getId());
		} else {
			userService.joinDefaultGroup(userInfo.getId());
		}
		// 회원가입 알림 메시지 발송
		AlertMessage userCreate = AlertMessage.USER_CREATE;
		String mailTitle = userCreate.getMailTitle();
		String title = userCreate.getTitle();
		String message = String.format(userCreate.getMessage(), userReqVO.getLastName() + userReqVO.getFirstName(),
			userReqVO.getEmail());
		eventPublisher.publishEvent(
			new AdminAlertEvent(AlertName.ADMIN_USER_JOIN, userInfo.getId(), mailTitle, title, message, null));
		MailAttribute mail = MailAttribute.USER_JOIN;
		// Mail Contents 작성
		List<MailDTO.Content> contents = List.of(MailDTO.Content.builder()
				.col1("사용자 이름 : ")
				.col2(userReqVO.getLastName() + userReqVO.getFirstName())
				.build(),
			MailDTO.Content.builder().col1("이메일 주소 : ").col2(userReqVO.getEmail()).build(),
			MailDTO.Content.builder()
				.col1("가입 일시 : ")
				.col2(userInfo.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build());
		List<UserDTO.UserInfo> adminList = userService.getAdminList();
		for (UserDTO.UserInfo admin : adminList) {
			// Mail 전송
			mailService.sendMail(MailDTO.builder()
				.subject(mail.getSubject())
				.title(String.format(mail.getTitle(), userReqVO.getLastName() + userReqVO.getFirstName(),
					userReqVO.getEmail()))
				.contents(contents)
				.subTitle(mail.getSubTitle())
				.footer(mail.getFooter())
				.receiverEmail(admin.getEmail())
				.build());
		}
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
	public UserDTO.UserInfo getUserInfoById(String userId) {
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
		// ADMIN 으로 권한 변경시, 설정 초기값 세팅
		if (authType == AuthType.ROLE_ADMIN) {
			alertService.initializeAdminAlertMappingSettings(userId);
		} else if (authType == AuthType.ROLE_USER) {
			alertService.deleteAdminAlertMappings(userId);
		}
	}

	@Override
	public void deleteUserById(List<String> userIdList) {
		userService.deleteUserById(userIdList);
	}

	@Override
	public List<SearchDTO> getUserAndGroupBySearch(String search) {
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
	public void updateUserInfoById(String id, UpdateUserDTO updateUserDTO) {
		userService.updateUserInfoById(id, updateUserDTO);
		UserDTO.UserInfo userInfo = userService.getUserInfoById(id);

		// 회원정보 변경 알림 메시지 발송
		AlertMessage userUpdate = AlertMessage.USER_UPDATE;
		String mailTitle = userUpdate.getMailTitle();
		String title = userUpdate.getTitle();
		String message = String.format(userUpdate.getMessage(), userInfo.getLastName() + userInfo.getFirstName());
		eventPublisher.publishEvent(
			new UserAlertEvent(AlertName.USER_UPDATE, mailTitle, title, message, userInfo.getId())
		);
		MailAttribute mail = MailAttribute.USER_UPDATE;
		// Mail Contents 작성
		List<MailDTO.Content> contents = List.of(
			MailDTO.Content.builder().col1("사용자 이름 : ").col2(userInfo.getLastName() + userInfo.getFirstName()).build(),
			MailDTO.Content.builder().col1("이메일 주소 : ").col2(userInfo.getEmail()).build()

		);
		// Mail 전송
		mailService.sendMail(MailDTO.builder()
			.subject(mail.getSubject())
			.title(
				String.format(mail.getTitle(), userInfo.getLastName() + userInfo.getFirstName(), userInfo.getEmail()))
			.subTitle(mail.getSubTitle())
			.contentTitle(mail.getContentTitle())
			.receiverEmail(userInfo.getEmail())
			.contents(contents)
			.footer(mail.getFooter())
			.build());

	}

	@Override
	public List<UserDTO.UserInfo> getAdminList() {
		return userService.getAdminList();
	}

	@Override
	public void joinAdmin(UserReqVO userReqVO) {
		userService.joinAdmin(userReqVO);
	}

	@Override
	public Set<String> getWorkspaceList(String userId, boolean isMyWorkspace) {
		UserDTO.UserInfo userInfo = userService.getUserInfoById(userId);
		List<String> workspaces = userInfo.getWorkspaces();
		if (!CollectionUtils.isEmpty(workspaces)) {
			if (isMyWorkspace) {
				return userInfo.getMyWorkspaces();
			} else {
				return userInfo.getAllWorkspaces();
			}
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public boolean isMyWorkspace(String userId, String workspaceName) {
		UserDTO.UserInfo userInfo = userService.getUserInfoById(userId);
		//ROLE_ADMIN을 가지고 있을 경우, 리턴 true
		if (userInfo.getAuth() == AuthType.ROLE_ADMIN) {
			return true;
		}
		if (StringUtils.isEmpty(workspaceName)) {
			return false;
		}

		Set<String> ownerWorkspace = userInfo.getWorkspaces()
			.stream()
			.filter(ws -> ws.contains("/owner"))
			.map(group -> group.split("/")[0])
			.collect(
				Collectors.toSet());

		return ownerWorkspace.contains(workspaceName);
	}

	@Override
	public boolean isAccessAuthorityWorkspace(String userId, String workspaceName) {
		UserDTO.UserInfo userInfo = userService.getUserInfoById(userId);
		//ROLE_ADMIN을 가지고 있을 경우, 리턴 true
		if (userInfo.getAuth() == AuthType.ROLE_ADMIN) {
			return true;
		}
		if (StringUtils.isEmpty(workspaceName)) {
			return false;
		}

		Set<String> workspaces = userInfo.getWorkspaces().stream()
			.filter(ws -> ws.contains("/owner") || ws.contains("/user"))
			.map(group -> group.split("/")[0])
			.collect(Collectors.toSet());

		return workspaces.contains(workspaceName);
	}

	@Override
	public boolean isAccessAuthorityWorkspaceNotAdmin(String userId, String workspaceName) {
		UserDTO.UserInfo userInfo = userService.getUserInfoById(userId);

		if (StringUtils.isEmpty(workspaceName)) {
			return false;
		}

		Set<String> workspaces = userInfo.getWorkspaces().stream()
			.filter(ws -> ws.contains("/owner") || ws.contains("/user"))
			.map(group -> group.split("/")[0])
			.collect(Collectors.toSet());

		return workspaces.contains(workspaceName);
	}

	@Override
	public WorkspaceRole getWorkspaceAuthority(String userId, String workspaceName) {
		UserDTO.UserInfo userInfo = userService.getUserInfoById(userId);

		Set<String> workspaces = userInfo.getWorkspaces().stream()
			.filter(ws -> ws.contains("/owner") || ws.contains("/user"))
			.map(group -> group.split("/")[0])
			.collect(Collectors.toSet());

		return workspaces.contains(workspaceName) ? WorkspaceRole.ROLE_OWNER : WorkspaceRole.ROLE_USER;
	}
}
