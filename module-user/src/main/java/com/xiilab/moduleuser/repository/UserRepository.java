package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.enumeration.AuthType;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;

@Component
public interface UserRepository {
	UserInfo joinUser(UserReqVO userReqVO);

	List<UserSummary> getUserList(FindDTO findDTO);

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
}
