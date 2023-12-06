package com.xiilab.moduleuser.repository;

import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.dto.UserInfo;

@Component
public class KeycloakRepository implements UserRepository{

	@Override
	public UserInfo getUserById(String id) {
		return null;
	}

	@Override
	public UserInfo getUserByUserName(String userEmail) {
		return null;
	}
}
