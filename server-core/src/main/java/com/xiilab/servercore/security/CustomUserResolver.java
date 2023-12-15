package com.xiilab.servercore.security;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.repository.KeycloakUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserResolver implements HandlerMethodArgumentResolver {
	private final KeycloakUserRepository repository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// userDto 가 파라미터에 포함되어 있는지 체크하여 true를 리턴한다.
		return parameter.getParameterType() == UserInfo.class;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Jwt principal = (Jwt)authentication.getPrincipal();
		LinkedTreeMap realmAccess = (LinkedTreeMap)principal.getClaims().get("realm_access");
		List<String> roles = (List<String>)realmAccess.get("roles");
		List<String> authList = roles.stream().filter(role -> role.contains("ROLE_"))
			.map(role -> role.replace("ROLE_", "")).toList();
		String auth = "";
		if (authList.contains("ADMIN")) {
			auth = "ADMIN";
		} else if (authList.contains("DEVELOPER")) {
			auth = "DEVELOPER";
		} else {
			auth = "USER";
		}

		return null;
	}

}
