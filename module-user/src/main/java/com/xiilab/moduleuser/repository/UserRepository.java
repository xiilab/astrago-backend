package com.xiilab.moduleuser.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;

@Component
public interface UserRepository {
	List<UserSummary> getUserList();
	UserInfo getUserByUserName(String userEmail);
}
