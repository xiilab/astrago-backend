package com.xiilab.moduleuser.service;

import static com.xiilab.moduleuser.dto.GroupCategory.*;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.repository.GroupRepository;
import com.xiilab.moduleuser.vo.GroupReqVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
	private final GroupRepository groupRepository;

	@Override
	public void createAccountGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfo) {
		GroupReqVO groupReqVO = GroupReqVO.builder()
			.name(groupReqDTO.getName())
			.description(groupReqDTO.getDescription())
			.groupCategory(ACCOUNT)
			.build();
		groupReqVO.setCreator(userInfo);
		//group 생성
		GroupSummaryDTO groupInfo = groupRepository.createGroup(groupReqVO);

		if(Objects.nonNull(groupReqDTO.getUsers())){
			//group에 member join
			groupRepository.joinMembersIntoGroup(groupInfo.getId(), groupReqDTO.getUsers());
		}
	}

	@Override
	public void createWorkspaceGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfoDTO) {
		//group 생성
		GroupReqVO groupReqVO = GroupReqVO.builder()
			.name(groupReqDTO.getName())
			.description(groupReqDTO.getDescription())
			.groupCategory(WORKSPACE)
			.createdBy(groupReqDTO.getCreatedBy())
			.build();
		groupReqVO.setCreator(userInfoDTO);
		GroupSummaryDTO groupInfo = groupRepository.createGroup(groupReqVO);
		//workspace 그룹의 childGroup 생성 및 유저 추가
		createWorkspaceChildGroup(groupInfo.getId(), groupReqDTO);
	}

	@Override
	public List<GroupSummaryDTO> getGroupList(String searchText) {
		return groupRepository.getGroupList(searchText);
	}

	@Override
	public GroupInfoDTO getGroupInfoById(String groupId) {
		return groupRepository.getGroupById(groupId);
	}

	@Override
	public void addGroupMember(String groupId, List<String> userIdList) {
		groupRepository.joinMembersIntoGroup(groupId, userIdList);
	}

	@Override
	public GroupUserDTO.SubGroupUserDto getGroupUsers(String groupId, AuthType authType) {
		return groupRepository.findUsersByGroupId(groupId, authType);
	}

	@Override
	public void deleteGroupById(String groupId) {
		groupRepository.deleteGroupById(groupId);
	}

	@Override
	public void deleteWorkspaceGroupByName(String groupName) {
		GroupInfoDTO groupInfo = groupRepository.getGroupInfoByCategoryAndName(WORKSPACE,
			groupName);
		groupRepository.deleteGroupById(groupInfo.getUid());
	}

	@Override
	public void deleteAccountGroupByName(String groupName) {
		GroupInfoDTO groupInfo = groupRepository.getGroupInfoByCategoryAndName(ACCOUNT, groupName);
		groupRepository.deleteGroupById(groupInfo.getUid());
	}

	private void createWorkspaceChildGroup(String parentId, GroupReqDTO groupReqDTO) {
		//owner 그룹 생성
		GroupSummaryDTO ownerGroup = groupRepository.createChildGroup(GroupReqVO.ChildGroupReqVO.builder()
			.name("owner")
			.description(groupReqDTO.getDescription())
			.parentGroupId(parentId)
			.createdBy(groupReqDTO.getCreatedBy())
			.build());
		//user 그룹 생성
		GroupSummaryDTO userGroup = groupRepository.createChildGroup(GroupReqVO.ChildGroupReqVO.builder()
			.name("user")
			.description(groupReqDTO.getDescription())
			.parentGroupId(parentId)
			.createdBy(groupReqDTO.getCreatedBy())
			.build());
		//생성한 owner group에 유저 추가
		groupRepository.joinMembersIntoGroup(ownerGroup.getId(), groupReqDTO.getUsers());
		//생성한 user group에 유저 추가
		groupRepository.joinMembersIntoGroup(userGroup.getId(), groupReqDTO.getUsers());
	}

	@Override
	public List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName) {
		return groupRepository.getWorkspaceMember(groupName);
	}
	@Override
	public void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList){
		groupRepository.deleteWorkspaceMemberByUserId(groupName, userIdList);
	}
	@Override
	public void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO addWorkspaceUsersDTO){
		groupRepository.addWorkspaceMemberByUserId(groupName, addWorkspaceUsersDTO);
	}
	@Override
	public List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search){
		return groupRepository.getWorkspaceMemberBySearch(groupName, search);
	}
	@Override
	public GroupUserDTO getWorkspaceOwner(String groupName){
		groupRepository.getWorkspaceOwner(groupName);
		return null;
	}

	@Override
	public UserDTO.SearchGroupAndUser getUserAndGroupBySearchText(String searchText) {
		return groupRepository.getUserAndGroupBySearchText(searchText);
	}

	@Override
	public void deleteGroupMemberByUserId(String groupId, List<String> userIdList) {
		groupRepository.deleteGroupMemberByUserId(groupId, userIdList);
	}

	@Override
	public void modifyAccountGroup(String groupId, GroupReqDTO.ModifyGroupDTO groupReqDTO) {
		groupRepository.modiGroupById(groupId, groupReqDTO);
	}
}
