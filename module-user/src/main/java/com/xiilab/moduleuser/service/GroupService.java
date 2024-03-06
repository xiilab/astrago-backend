package com.xiilab.moduleuser.service;

import java.util.List;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserInfoDTO;

public interface GroupService {
	//사용자 그룹 생성
	void createAccountGroup(GroupReqDTO groupReqDTO, UserInfoDTO userInfo);

	//워크스페이스 그룹 생성
	void createWorkspaceGroup(GroupReqDTO groupReqDTO);

	//그룹 전체 목록 조회
	List<GroupSummaryDTO> getGroupList();

	//그룹 상세 조회
	GroupInfoDTO getGroupInfoById(String groupId);

	//그룹 멤버 추가
	void addGroupMember(String groupId, List<String> userIdList);

	//그룹 안의 유저 리스트 조회
	GroupUserDTO.SubGroupUserDto getGroupUsers(String groupId);

	//그룹 Id로 group을 삭제
	void deleteGroupById(String groupId);

	void deleteWorkspaceGroupByName(String groupName);

	void deleteAccountGroupByName(String groupName);

	//그룹 안의 유저 리스트 조회
	List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName);

	void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList);
	void addWorkspaceMemberByUserId(String groupName, List<String> userIdList);
	List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search);
	GroupUserDTO getWorkspaceOwner(String groupName);

	List<UserDTO.SearchUser> getUserAndGroupBySearchText(String searchText);
}
