package com.xiilab.moduleuser.common;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
@Configuration
@Component
public class KeycloakConfig {
	@Value("${admin.realm}")
	private String realm;
	@Value("${keycloak.auth-server-url}")
	private String keycloakUrl;
	@Value("${admin.client}")
	private String client;
	@Value("${admin.name}")
	private String name;
	@Value("${admin.password}")
	private String password;
	@Value("${admin.secret}")
	private String secret;

	@Bean
	public Keycloak getKeycloakClient() {
		return Keycloak.getInstance(
			keycloakUrl,
			realm,
			name,
			password,
			client,
			secret);
	}
}
