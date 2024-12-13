package com.xiilab.servercore.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserResolver implements HandlerMethodArgumentResolver {
	private final UserRepository repository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// userDto 가 파라미터에 포함되어 있는지 체크하여 true를 리턴한다.
		return parameter.getParameterType() == UserDTO.UserInfo.class;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Jwt principal = (Jwt)authentication.getPrincipal();
		return repository.getUserInfoById(principal.getSubject());
	}
}
