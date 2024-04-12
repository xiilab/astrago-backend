package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;

@Component
public interface UserRepository {
	UserInfo joinUser(UserReqVO userReqVO);

	UserDTO.PageUsersDTO getUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition);

	UserInfo getUserInfoById(String userId);

	List<UserSummary> getUserListSearchByAttribute(String attribute);

	void updateUserAttribute(List<String> userIdList, Map<String, String> map);

	void updateUserActivationYN(List<String> userIdList, boolean activationYN);

	void deleteUserById(List<String> userIdList);

	void updateUserRole(String userId, AuthType authType);

	void joinGroup(String groupId, String userId);

	void joinDefaultGroup(String userId);

	void resetUserPassWord(String userId);

	List<SearchDTO> getUserAndGroupBySearch(String search);

	void updateUserInfoById(String id, UpdateUserDTO updateUserDTO);

	void updateUserEnable(String id, boolean enable);

	UserDTO.UserInfo getUserById(String id);

	UserDTO.PageUsersDTO getWaitingApprovalUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition);

	List<UserInfo> getAdminList();

	void joinAdmin(UserReqVO userReqVO);
}
