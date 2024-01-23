package com.xiilab.servercore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.xiilab.servercore.common.entity.User;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditorConfig {
	@Bean
	AuditorAware<User> auditorProvider() {
		return new AuditorAwareImpl();
	}
}
