package com.xiilab.servercore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.xiilab.servercore.workload.service.TerminalWebSocketHandler;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {
	/**
	 * timeout 0
	 *
	 * @return
	 */
	@Bean
	public WebSocketContainerFactoryBean createWebSocketContainer() {
		return new WebSocketContainerFactoryBean();
	}

	@Bean
	public WebSocketHandler terminalSocket() {
		return new PerConnectionWebSocketHandler(TerminalWebSocketHandler.class);
	}


	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(terminalSocket(), "/ws/terminal").setAllowedOrigins("*");
	}
}
