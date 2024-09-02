package com.xiilab.servercore.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<RegUser> {
	@Autowired
	private UserRepository repository;

	@Override
	public Optional<RegUser> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder
			.getContext()
			.getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
			authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.empty();
		}
		Jwt principal = (Jwt)authentication.getPrincipal();
		UserDTO.UserInfo userInfo = repository.getUserInfoById(principal.getSubject());

		return Optional.of(new RegUser(userInfo.getId(), userInfo.getUserName(), userInfo.getUserFullName()));
	}
}
