package com.xiilab.servercore.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.repository.KeycloakUserRepository;
import com.xiilab.servercore.common.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<User> {
	@Autowired
	private KeycloakUserRepository repository;

	@Override
	public Optional<User> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder
			.getContext()
			.getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
			authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.empty();
		}
		Jwt principal = (Jwt)authentication.getPrincipal();
		UserInfo userInfo = repository.getUserInfoById(principal.getSubject());

		return Optional.of(new User(userInfo.getId(), userInfo.getUserName()));
	}
}
