package com.xiilab.servercore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.xiilab.servercore.common.entity.RegUser;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditorConfig {
	@Bean
	AuditorAware<RegUser> auditorProvider() {
		return new AuditorAwareImpl();
	}
}
