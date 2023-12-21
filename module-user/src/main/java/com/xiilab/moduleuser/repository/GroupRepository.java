package com.xiilab.moduleuser.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.vo.GroupModiVO;
import com.xiilab.moduleuser.vo.GroupReqVO;

@Repository
public interface GroupRepository {
    List<GroupSummaryDTO> getGroupList(FindDTO findDTO);
	GroupInfoDTO getGroupById(String groupId);
	GroupSummaryDTO createGroup(GroupReqVO groupReqVO);
	GroupSummaryDTO createChildGroup(GroupReqVO.ChildGroupReqVO groupReqVO);
	void deleteGroupById(String groupId);
	void modiGroupById(String groupId, GroupModiVO groupModiVO);
	List<GroupUserDTO> findUsersByGroupId(String groupId);
	void joinMembersIntoGroup(String groupId, List<String> userIds);
}
