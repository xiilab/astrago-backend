package com.xiilab.servercore.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.GroupService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupFacadeServiceImpl implements GroupFacadeService {
	private final GroupService groupService;

	@Override
	public void createAccountGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfo) {
		groupService.createAccountGroup(groupReqDTO, userInfo);
	}

	@Override
	public void createWorkspaceGroup(GroupReqDTO groupReqDTO) {
		groupService.createWorkspaceGroup(groupReqDTO);
	}

	@Override
	public List<GroupSummaryDTO> getGroupList() {
		return groupService.getGroupList();
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
	public GroupUserDTO.SubGroupUserDto getGroupUsers(String groupId) {
		return groupService.getGroupUsers(groupId);
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
	}
	@Override
	public void addWorkspaceMemberByUserId(String groupName, List<String> userIdList){
		groupService.addWorkspaceMemberByUserId(groupName, userIdList);
	}
	@Override
	public List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search){
		return groupService.getWorkspaceMemberBySearch(groupName, search);
	}

	@Override
	public List<UserDTO.SearchUser> getUserAndGroupBySearchText(String searchText) {
		return groupService.getUserAndGroupBySearchText(searchText);
	}
}
