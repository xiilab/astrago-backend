package com.xiilab.servercore.user.service;

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
import com.xiilab.modulecommon.enums.WorkspaceRole;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeServiceImpl;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.alert.systemalert.service.AlertService;
import com.xiilab.servercore.pin.service.PinService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {
	private final UserService userService;
	private final AlertService alertService;
	private final ApplicationEventPublisher eventPublisher;
	private final WorkspaceModuleFacadeServiceImpl workspaceModuleFacadeService;
	private final PinService pinService;

	@Override
	public void joinUser(UserReqVO userReqVO, String groupId) {
		// 회원가입
		UserDTO.UserInfo userInfo = userService.joinUser(userReqVO);
		if (StringUtils.isNotBlank(groupId)) {
			userService.joinDefaultGroup(userInfo.getId());
			userService.joinGroup(groupId, userInfo.getId());
		} else {
			userService.joinDefaultGroup(userInfo.getId());
		}
		createDefaultWorkspace(userInfo);

		// 회원가입 알림 메시지 발송
		AlertMessage userCreate = AlertMessage.USER_CREATE;
		String mailTitle = userCreate.getMailTitle();
		String title = userCreate.getTitle();
		String message = String.format(userCreate.getMessage(), userReqVO.getLastName() + userReqVO.getFirstName(),
			userReqVO.getEmail());

		MailDTO mailDTO = MailServiceUtils.createUserMail(userReqVO.getLastName() + userReqVO.getFirstName(),
			userReqVO.getEmail(), userInfo.getJoinDate());

		eventPublisher.publishEvent(new AdminAlertEvent(AlertName.ADMIN_USER_JOIN, userInfo.getId(), mailTitle, title, message, null, mailDTO));
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
			new UserAlertEvent(AlertName.USER_UPDATE, mailTitle, title, message, userInfo.getId(), null)
		);
	}

	@Override
	public List<UserDTO.UserInfo> getAdminList() {
		return userService.getAdminList();
	}

	@Override
	public void joinAdmin(UserReqVO userReqVO) {
		String userId = userService.joinAdmin(userReqVO);
		//admin alert 정보 입력
		alertService.initializeAdminAlertMappingSettings(userId);
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

	/**
	 * 사용자 기본 워크스페이스 생성 후 pin으로 등록
	 * @param userInfo
	 */
	private void createDefaultWorkspace(UserDTO.UserInfo userInfo) {
		// 사용자 기본 워크스페이스 생성
		WorkspaceDTO.ResponseDTO workspace = workspaceModuleFacadeService.createWorkspace(CreateWorkspaceDTO.builder()
			.name(userInfo.getUserFullName() + "'s workspace")
			.description(userInfo.getUserFullName() + "'s default workspace.")
			.creatorId(userInfo.getId())
			.creatorUserName(userInfo.getUserName())
			.creatorFullName(userInfo.getUserFullName())
			.build());

		// 생성된 workspace pin으로 등록
		pinService.createPin(workspace.getResourceName(), PinType.WORKSPACE, userInfo);
	}
}
