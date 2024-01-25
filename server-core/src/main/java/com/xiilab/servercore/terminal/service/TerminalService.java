package com.xiilab.servercore.terminal.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TerminalService {
	// private final TerminalModuleService terminalModuleService;
	/**
	 * WebTerminal로 Command 전송하는 메소드
	 * @param command 전송될 Command
	 * @return
	 */
	public String sendMessage(String command){

		// terminalModuleService.
		return "헤헤헤헤헤";
	}
}
