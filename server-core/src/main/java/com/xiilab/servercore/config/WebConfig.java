package com.xiilab.servercore.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.xiilab.moduleuser.repository.KeycloakUserRepository;
import com.xiilab.servercore.security.CustomUserResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final KeycloakUserRepository repository;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new CustomUserResolver(repository));
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 기본URL: docs/swagger-ui/index.html
		registry.addResourceHandler("/api-docs/**").addResourceLocations("classpath:/static/docs/swagger-ui/");
	}
}
