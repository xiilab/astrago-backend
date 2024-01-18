package com.xiilab.servercore.user.service;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.common.dto.SearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupFacadeServiceImpl implements GroupFacadeService {
	private final GroupService groupService;

	@Override
	public void createAccountGroup(GroupReqDTO groupReqDTO) {
		groupService.createAccountGroup(groupReqDTO);
	}

	@Override
	public void createWorkspaceGroup(GroupReqDTO groupReqDTO) {
		groupService.createWorkspaceGroup(groupReqDTO);
	}

	@Override
	public List<GroupSummaryDTO> getGroupList(SearchCondition searchCondition) {
		FindDTO findDTO = FindDTO.builder()
			.option(searchCondition.getOption())
			.keyword(searchCondition.getKeyword())
			.build();
		return groupService.getGroupList(findDTO);
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
	public List<GroupUserDTO> getGroupUsers(String groupId) {
		return groupService.getGroupUsers(groupId);
	}

	@Override
	public void deleteGroupById(String groupId) {
		groupService.deleteGroupById(groupId);
	}
}
