package com.xiilab.servercore.workload.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.inject.Provider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TerminalWebSocketHandler extends TextWebSocketHandler {
	private final Provider<TerminalService> terminalServiceProvider;
	private final TerminalHandler terminalHandler;

	protected TerminalService terminalService;

	/*
	 * 웹소켓 서버 시작시 동작하는 메서드
	 * (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 세션 입력
		terminalService = terminalServiceProvider.get();
		terminalService.setWebSocketSession(session);
		super.afterConnectionEstablished(session);
	}

	/*
	 * 클라이언트가 서버로 메세지 수신할경우 동작하는 메서드
	 * 소켓에 다음과 같이 전송될 예정
	 * (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#handleTextMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.TextMessage)
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		terminalHandler.handler(message.getPayload(), terminalService);
	}





	/* (non-Javadoc)
	 * @see org.springframework.web.socket.handler.AbstractWebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		terminalService.exitShell();
		terminalService.disConnect();
		super.afterConnectionClosed(session, status);
	}

}
