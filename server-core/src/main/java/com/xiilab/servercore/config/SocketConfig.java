package com.xiilab.servercore.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.servercore.websocket.handler.WorkloadLogHandler;
import com.xiilab.servercore.workload.service.TerminalWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {
	private final WorkloadModuleFacadeService workloadModuleFacadeService;

	// 워크로드 로그 세션 Map
	@Bean
	public Map<String, WebSocketSession> workloadLogWebSocketSessionMap() {
		return new ConcurrentHashMap<>();
	}

	/**
	 * timeout 0
	 *
	 * @return
	 */
	@Bean
	public WebSocketContainerFactoryBean createWebSocketContainer() {
		return new WebSocketContainerFactoryBean();
	}

	// 워크로드 로그 세션 핸들러
	@Bean
	public WebSocketHandler workloadLogSocketHandler() {
		return new WorkloadLogHandler(workloadModuleFacadeService, workloadLogWebSocketSessionMap());
	}

	@Bean
	public WebSocketHandler terminalSocket() {
		return new PerConnectionWebSocketHandler(TerminalWebSocketHandler.class);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(terminalSocket(), "/ws/workload/terminal").setAllowedOrigins("*");
		registry.addHandler(workloadLogSocketHandler(), "/ws/workload/log").setAllowedOrigins("*");
	}
}

