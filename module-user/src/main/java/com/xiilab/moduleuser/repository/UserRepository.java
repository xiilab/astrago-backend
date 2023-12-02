package com.xiilab.moduleuser.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.moduleuser.dto.UserInfo;

@Repository
public interface UserRepository {
	UserInfo getUserById(String id);

	UserInfo getUserByUserName(String userEmail);
}
