package com.xiilab.moduleuser.service;

import java.util.List;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.vo.UserReqVO;

public interface UserService {
	//회원가입
	UserInfo joinUser(UserReqVO userReqVO);

	//사용자 리스트 조회
	UserDTO.PageUsersDTO getUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition);

	//사용자 승인 신청 계정 리스트 조회
	UserDTO.PageUsersDTO getWaitingApprovalUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition);

	//사용자 상세 조회
	UserInfo getUserInfoById(String userId);

	//사용자 승인/거절 업데이트
	void updateUserApprovalYN(List<String> userIdList, boolean approvalYN);

	//사용자 활성화/비활성화
	void updateUserActivationYN(List<String> userIdList, boolean activationYN);

	void resetUserPassWord(String userId);

	void updateUserRole(String userId, AuthType authType);

	void joinGroup(String groupId, String userId);
	void joinDefaultGroup(String userId);

	void deleteUserById(List<String> userId);
	List<SearchDTO> getUserAndGroupBySearch(String string);
	void updateUserInfoById(String id, UpdateUserDTO updateUserDTO);

	void updateUserEnable(String id, boolean enable);

	UserDTO.UserInfo getUserById(String id);
}
