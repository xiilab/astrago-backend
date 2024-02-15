package com.xiilab.servercore.user.service;

import java.util.List;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;
import com.xiilab.servercore.common.dto.SearchCondition;

public interface UserFacadeService {
	//회원가입
	UserInfo joinUser(UserReqVO userReqVO, String groupId);

	//사용자 리스트 조회
	List<UserSummary> getUserList(SearchCondition searchCondition);

	//사용자 승인 신청 계정 리스트 조회
	List<UserSummary> getWaitingApprovalUserList();

	//사용자 상세 조회
	UserInfo getUserInfoById(String userId);

	//사용자 승인/거절 업데이트
	void updateUserApprovalYN(List<String> userIdList, boolean approvalYN);

	//사용자 활성화/비활성화
	void updateUserActivationYN(List<String> userIdList, boolean activationYN);

	void resetUserPassWord(String userId);

	void updateUserRole(String userId, AuthType authType);

	void deleteUserById(List<String> userIdList);
	List<SearchDTO> getUserAndGroupBySearch(String search);
}
