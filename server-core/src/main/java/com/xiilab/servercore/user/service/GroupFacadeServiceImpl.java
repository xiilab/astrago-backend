package com.xiilab.servercore.user.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.GroupService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupFacadeServiceImpl implements GroupFacadeService {
	private final GroupService groupService;
	private final WorkspaceAlertService workspaceAlertService;

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
	public void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList){
		groupService.deleteWorkspaceMemberByUserId(groupName, userIdList);

		//삭제 된 멤버들 알람 매핑 데이터 삭제
		for (String userId : userIdList) {
			workspaceAlertService.deleteWorkspaceAlertMappingByUserIdAndWorkspaceName(userId, groupName);
		}

	}
	@Override
	public void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO addWorkspaceUsersDTO){
		Set<String> addUserIds = groupService.addWorkspaceMemberByUserId(groupName, addWorkspaceUsersDTO);

		//추가 된 멤버들 알람 매핑 데이터 저장
		for (String userId : addUserIds) {
			workspaceAlertService.initWorkspaceAlertMapping(AlertRole.USER, userId, groupName);
		}
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
