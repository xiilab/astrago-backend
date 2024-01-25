package com.xiilab.servercore.terminal.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.xiilab.servercore.terminal.service.TerminalService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TerminalController {
	private final TerminalService terminalService;
	@MessageMapping("/message")
	@SendTo("/topic/result")
	public String executeCommand(String command) {
		// 입력된 명령어 실행 및 결과 반환
		return terminalService.sendMessage(command);
	}

}
