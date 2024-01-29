package com.xiilab.servercore.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.servercore.websocket.handler.WorkloadLogHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(workloadLogSocketHandler(), "/workload/log");
	}

	@Bean
	public WebSocketHandler workloadLogSocketHandler() { return new WorkloadLogHandler(workloadModuleFacadeService, workloadLogWebSocketSessionMap()); }

	// 워크로드 로그 세션 관리
	@Bean
	public Map<String, WebSocketSession> workloadLogWebSocketSessionMap() {
		 return new ConcurrentHashMap<>();
	}
}
