package com.xiilab.servercore.workload.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.servercore.workload.dto.TerminalMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TerminalHandler {
	/**
	 * json text 파싱
	 *
	 * @param message
	 * @param terminalService
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SecurityException
	 */
	public void handler(String message, TerminalService terminalService) throws IOException, InterruptedException, SecurityException {
		TerminalMessage messageMap;
		try {
			// 메세지 형식 {repositoryType: "TERMINAL_COMMAND", command: ""} (json) 을  Map 으로 변경
			messageMap = getMessageMap(message);
		} catch (JsonProcessingException e) {
			// json 에러일때 웝소켓으로 수신
			terminalService.sendAlert(e.getMessage());
			return;
		}

		// 메세제 타입에 따른 분류처리
		switch (messageMap.getMessageType()) {
			// TERMINAL_HELP : 타입 제공
			case TERMINAL_HELP:
				terminalService.sendLogMessage("TERMINAL_HOST | TERMINAL_INIT | TERMINAL_COMMAND | TERMINAL_RESIZE | TERMINAL_ALERT | TERMINAL_LOG ");
				break;

			// TERMINAL_AUTH : server 에 유저 정보 입력
			case TERMINAL_HOST:
				terminalService.setMessageInfo(messageMap);
				terminalService.sendLogMessage("SET IP ADDRESS AND SSH USER");
				break;

			// TERMINAL_INIT & TERMINAL_READY & setup
			// SSH 세션 연결및 통신을 위한 쓰레드 생성
			case TERMINAL_INIT:
				terminalService.onTerminalInit();
				break;
			// TERMINAL_COMMAND : 받은 메세지를 해당 쓰레드에 내부에 있는 스트림에 메세지 입력
			case TERMINAL_COMMAND:
				terminalService.onCommand(messageMap.getCommand());
				break;
			// 터미널 리사이즈
			case TERMINAL_RESIZE:
				terminalService.onTerminalResize(messageMap.getColumns(), messageMap.getRows());
				terminalService.sendLogMessage("RESIZE COMPLETE");
				break;
			default:
				throw new IOException("Unrecognized action");
		}
	}

	/**
	 * JSON Text 을 Class 으로 파싱
	 *
	 * @param message
	 * @return
	 */
	private TerminalMessage getMessageMap(String message) throws JsonProcessingException {
		try {
			return new ObjectMapper().readValue(message, TerminalMessage.class);
		} catch (JsonProcessingException e) {
			log.error("{}", e);
			throw e;
		}
	}
}
