package com.xiilab.moduleuser.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupCategory;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.vo.GroupModiVO;
import com.xiilab.moduleuser.vo.GroupReqVO;

@Repository
public interface GroupRepository {
	List<GroupSummaryDTO> getGroupList(String searchText);

	List<GroupSummaryDTO> getGroupListByCategory(GroupCategory groupCategory);

	GroupInfoDTO getGroupInfoByCategoryAndName(GroupCategory groupCategory, String groupName);

	GroupInfoDTO getGroupById(String groupId);

	GroupSummaryDTO createGroup(GroupReqVO groupReqVO);

	GroupSummaryDTO createChildGroup(GroupReqVO.ChildGroupReqVO groupReqVO);

	void deleteGroupById(String groupId);

	void modiGroupById(String groupId, GroupReqDTO.ModifyGroupDTO groupReqDTO);

	GroupUserDTO.SubGroupUserDto findUsersByGroupId(String groupId);

	void joinMembersIntoGroup(String groupId, List<String> userIds);
	List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName);

	void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList);
	void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO userIdList);
	List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search);
	GroupUserDTO getWorkspaceOwner(String groupName);

	UserDTO.SearchGroupAndUser getUserAndGroupBySearchText(String searchText);

	void deleteGroupMemberByUserId(String groupId, List<String> userIdList);
}
