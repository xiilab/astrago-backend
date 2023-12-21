package com.xiilab.moduleuser.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.GroupCategory;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.repository.GroupRepository;
import com.xiilab.moduleuser.vo.GroupReqVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
	private final GroupRepository groupRepository;

	@Override
	public void createAccountGroup(GroupReqDTO groupReqDTO) {
		//group 생성
		GroupSummaryDTO groupInfo = groupRepository.createGroup(
			GroupReqVO.builder()
				.name(groupReqDTO.getName())
				.description(groupReqDTO.getDescription())
				.groupCategory(GroupCategory.ACCOUNT)
				.createdBy(groupReqDTO.getCreatedBy())
				.build());
		//group에 member join
		groupRepository.joinMembersIntoGroup(groupInfo.getId(), groupReqDTO.getUsers());
	}

	@Override
	public void createWorkspaceGroup(GroupReqDTO groupReqDTO) {
		//group 생성
		GroupSummaryDTO groupInfo = groupRepository.createGroup(
			GroupReqVO.builder()
				.name(groupReqDTO.getName())
				.description(groupReqDTO.getDescription())
				.groupCategory(GroupCategory.WORKSPACE)
				.createdBy(groupReqDTO.getCreatedBy())
				.build());
		//workspace 그룹의 childGroup 생성 및 유저 추가
		createWorkspaceChildGroup(groupInfo.getId(), groupReqDTO);
	}

	@Override
	public List<GroupSummaryDTO> getGroupList(FindDTO findDTO) {
		return groupRepository.getGroupList(findDTO);
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
	public List<GroupUserDTO> getGroupUsers(String groupId) {
		return groupRepository.findUsersByGroupId(groupId);
	}

	@Override
	public void deleteGroupById(String groupId) {
		groupRepository.deleteGroupById(groupId);
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
		groupRepository.joinMembersIntoGroup(ownerGroup.getId(), List.of(groupReqDTO.getCreatedUserId()));
		//생성한 user group에 유저 추가
		groupRepository.joinMembersIntoGroup(userGroup.getId(), groupReqDTO.getUsers());
	}
}
