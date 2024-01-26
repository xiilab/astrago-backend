package com.xiilab.servercore.workload.enumeration;

public enum TerminalMessageType {
	TERMINAL_HELP, // TERMINAL_HELP : 타입 제공
	TERMINAL_HOST, // TERMINAL_AUTH : server 에 server , 유저 정보 입력
	TERMINAL_INIT, // TERMINAL_INIT & TERMINAL_READY & setup SSH 세션 연결및 통신을 위한 쓰레드 생성
	TERMINAL_COMMAND, // TERMINAL_COMMAND : 받은 메세지를 해당 쓰레드에 내부에 있는 스트림에 메세지 입력
	TERMINAL_RESIZE // 터미널 리사이즈
}
