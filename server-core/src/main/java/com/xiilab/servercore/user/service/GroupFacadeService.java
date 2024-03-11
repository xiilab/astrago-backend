package com.xiilab.servercore.user.service;

import java.util.List;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;

public interface GroupFacadeService {
	//사용자 그룹 생성
	void createAccountGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfo);

	//워크스페이스 그룹 생성
	void createWorkspaceGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfoDTO);

	//그룹 전체 목록 조회
	List<GroupSummaryDTO> getGroupList(String searchText);

	//그룹 상세 조회
	GroupInfoDTO getGroupInfoById(String groupId);

	//그룹 멤버 추가
	void addGroupMember(String groupId, List<String> userIdList);

	//그룹 안의 유저 리스트 조회
	GroupUserDTO.SubGroupUserDto getGroupUsers(String groupId, AuthType authType);

	//그룹 Id로 group을 삭제
	void deleteGroupById(String groupId);

	// 워크스페이스 그룹 사용자 조회
	List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName);
	void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList);
	void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO userIdList);
	List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search);

	UserDTO.SearchGroupAndUser getUserAndGroupBySearchText(String searchText);

	void deleteGroupMemberByUserId(String groupId, List<String> userIdList);

	void modifyAccountGroup(String groupId, GroupReqDTO.ModifyGroupDTO groupReqDTO);
}
