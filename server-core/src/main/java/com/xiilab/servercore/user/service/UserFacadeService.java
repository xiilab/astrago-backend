package com.xiilab.servercore.user.service;

import java.util.List;
import java.util.Set;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.WorkspaceRole;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.vo.UserReqVO;

public interface UserFacadeService {
	//회원가입
	void joinUser(UserReqVO userReqVO, String groupId);

	//사용자 리스트 조회
	UserDTO.PageUsersDTO getUserList(PageInfo pageInfo, UserSearchCondition searchCondition);

	//사용자 승인 신청 계정 리스트 조회
	UserDTO.PageUsersDTO getWaitingApprovalUserList(PageInfo pageInfo, UserSearchCondition searchCondition);

	//사용자 상세 조회
	UserDTO.UserInfo getUserInfoById(String userId);

	// 사용자 정보 수정
	void updateUserInfoById(String id, UpdateUserDTO updateUserDTO);

	// 사용자 워크스페이스 생성 제한개수 업데이트
	void updateUserWorkspaceCreateLimit(List<String> userIdList, Integer createLimitCount);

	//사용자 승인/거절 업데이트
	void updateUserApprovalYN(List<String> userIdList, boolean approvalYN);

	//사용자 활성화/비활성화
	void updateUserActivationYN(List<String> userIdList, boolean activationYN);

	void resetUserPassWord(String userId);

	void updateUserRole(String userId, AuthType authType);

	void deleteUserById(List<String> userIdList);

	List<SearchDTO> getUserAndGroupBySearch(String search);

	void updateUserEnable(String id, boolean enable);

	UserDTO.UserInfo getUserById(String id);

	void updateUserWorkspaceCreateLimitById(String id, Integer workspaceCreateLimit);

	List<UserDTO.UserInfo> getAdminList();

	void joinAdmin(UserReqVO userReqVO);

	Set<String> getWorkspaceList(String userId, boolean isMyWorkspace);

	boolean isMyWorkspace(String userId, String workspaceName);

	boolean isAccessAuthorityWorkspace(String userId, String workspaceName);

	boolean isAccessAuthorityWorkspaceNotAdmin(String userId, String workspaceName);

	WorkspaceRole getWorkspaceAuthority(String userId, String workspaceName);
}
