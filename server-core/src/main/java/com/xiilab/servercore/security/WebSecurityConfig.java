package com.xiilab.servercore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.xiilab.servercore.common.filter.LicenseFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	public static final String ADMIN = "admin";
	public static final String USER = "user";
	private final JwtAuthConverter jwtAuthConverter;
	private final LicenseFilter licenseFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()
			// .requestMatchers( "/api/v1/**").permitAll()
			.requestMatchers("/error").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/core/user").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/core/group").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/core/user/join").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/core/user/admin").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/core/user/admin").permitAll()
			.requestMatchers("/api/v1/core/license/**").permitAll()
			.requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/core/alertManager/receive").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/v1/core/smtp").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/core/boards/contents/file/**").permitAll()
			.requestMatchers("/ws/**").permitAll()
			.requestMatchers("/api/v1/core/tus/**").permitAll()
			.requestMatchers("/actuator/**").permitAll()
			.anyRequest().authenticated());

		http.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter)));
		http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(licenseFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
