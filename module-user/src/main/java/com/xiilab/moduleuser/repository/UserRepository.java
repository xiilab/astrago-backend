package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;

@Component
public interface UserRepository {
	UserInfo joinUser(UserReqVO userReqVO);
	List<UserSummary> getUserList();
	UserInfo getUserInfoById(String userId);
	List<UserSummary> getUserListSearchByAttribute(String attribute);
	void updateUserAttribute(String userId, Map<String,String> map);
	void updateUserActivationYN(String userId, boolean activationYN);
	void deleteUserById(String userId);
	void updateUserRole(String userId, AuthType authType);
	void joinGroup(String groupId, String userId);
}
