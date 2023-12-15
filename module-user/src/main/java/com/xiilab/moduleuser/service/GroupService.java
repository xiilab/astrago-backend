package com.xiilab.moduleuser.service;

import java.util.List;

import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;

public interface GroupService {
	//그룹 생성
	void createAccountGroup(GroupReqDTO groupReqDTO);
	void createWorkspaceGroup(GroupReqDTO groupReqDTO);
	//그룹 전체 목록 조회
	List<GroupSummaryDTO> getGroupList();
	//그룹 상세 조회
	GroupInfoDTO getGroupInfo(String groupId);
	//그룹 멤버 추가
	void addGroupMember(String groupId, String userId);
	//그룹 안의 유저 리스트 조회
	List<GroupUserDTO> getGroupUsers(String groupId);
	void deleteGroupById(String groupId);
}
