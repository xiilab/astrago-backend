package com.xiilab.servercore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET,"/docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/user").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/user/join").permitAll()
                .requestMatchers(HttpMethod.GET,"/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated());

        http.oauth2ResourceServer((oauth2) -> oauth2
            .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter)));
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable());
        http.csrf().disable();
        return http.build();
    }
}
