package com.xiilab.servercore.user.service;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupFacadeServiceImpl implements GroupFacadeService {
	private final GroupService groupService;
	private final WorkspaceAlertService workspaceAlertService;
	private final ApplicationEventPublisher publisher;
	private final WorkspaceService workspaceService;
	@Override
	public void createAccountGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfo) {
		groupService.createAccountGroup(groupReqDTO, userInfo);
	}

	@Override
	public void createWorkspaceGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfoDTO) {
		groupService.createWorkspaceGroup(groupReqDTO, userInfoDTO);
	}

	@Override
	public List<GroupSummaryDTO> getGroupList(String searchText) {
		return groupService.getGroupList(searchText);
	}

	@Override
	public GroupInfoDTO getGroupInfoById(String groupId) {
		return groupService.getGroupInfoById(groupId);
	}

	@Override
	public void addGroupMember(String groupId, List<String> userIdList) {
		groupService.addGroupMember(groupId, userIdList);
	}

	@Override
	public GroupUserDTO.SubGroupUserDto getGroupUsers(String groupId, AuthType authType) {
		return groupService.getGroupUsers(groupId, authType);
	}

	@Override
	public void deleteGroupById(String groupId) {
		groupService.deleteGroupById(groupId);
	}

	@Override
	public List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName) {
		return groupService.getWorkspaceMember(groupName);
	}

	@Override
	public void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList, UserInfoDTO userInfoDTO){
		groupService.deleteWorkspaceMemberByUserId(groupName, userIdList);

		//삭제 된 멤버들 알람 매핑 데이터 삭제
		for (String userId : userIdList) {
			workspaceAlertService.deleteWorkspaceAlertMappingByUserIdAndWorkspaceName(userId, groupName);
		}

		sendModifyWorkspaceMemberEvent(groupName, userInfoDTO);
	}
	@Override
	public void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO addWorkspaceUsersDTO, UserInfoDTO userInfoDTO){
		Set<String> addUserIds = groupService.addWorkspaceMemberByUserId(groupName, addWorkspaceUsersDTO);

		//추가 된 멤버들 알람 매핑 데이터 저장
		for (String userId : addUserIds) {
			workspaceAlertService.initWorkspaceAlertMapping(AlertRole.USER, userId, groupName);
		}
		sendModifyWorkspaceMemberEvent(groupName, userInfoDTO);
	}

	private void sendModifyWorkspaceMemberEvent(String groupName, UserInfoDTO userInfoDTO) {
		WorkspaceDTO.ResponseDTO workspace = workspaceService.getWorkspaceByName(groupName);
		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(workspace.getResourceName())
			.build();

		String workspaceName = workspace.getName();
		String emailTitle = String.format(AlertMessage.WORKSPACE_MEMBER_UPDATE.getMailTitle(), workspaceName);
		String title = AlertMessage.WORKSPACE_MEMBER_UPDATE.getTitle();
		String message = String.format(AlertMessage.WORKSPACE_MEMBER_UPDATE.getMessage(), workspaceName);

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.OWNER, AlertName.OWNER_WORKSPACE_MEMBER_UPDATE,
			userInfoDTO.getId(), workspace.getCreatorId(), emailTitle, title, message, groupName, pageNaviParam);

		publisher.publishEvent(workspaceUserAlertEvent);
	}

	@Override
	public List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search){
		return groupService.getWorkspaceMemberBySearch(groupName, search);
	}

	@Override
	public UserDTO.SearchGroupAndUser getUserAndGroupBySearchText(String searchText, AuthType authType) {
		return groupService.getUserAndGroupBySearchText(searchText, authType);
	}

	@Override
	public void deleteGroupMemberByUserId(String groupId, List<String> userIdList) {
		groupService.deleteGroupMemberByUserId(groupId, userIdList);
	}

	@Override
	public void modifyAccountGroup(String groupId, GroupReqDTO.ModifyGroupDTO groupReqDTO) {
		groupService.modifyAccountGroup(groupId, groupReqDTO);
	}
}
