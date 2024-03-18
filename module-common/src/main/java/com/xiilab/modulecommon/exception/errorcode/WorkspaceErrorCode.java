package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkspaceErrorCode implements ErrorCode {

	WORKSPACE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 이름의 워크스페이스가 없습니다."),
	WORKSPACE_PIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 pin이 추가되었습니다."),
	WORKSPACE_USER_ADD_FAIL(HttpStatus.NOT_FOUND.value(), "워크스페이스에 회원 추가 실패하였습니다."),
	NOT_FOUND_WORKSPACE(HttpStatus.NOT_FOUND.value(), "해당 이름의 워크스페이스가 없습니다."),
	WORKSPACE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "해당 워크스페이스에 접근 권한이 없습니다."),

	;

	private final int code;
	private final String message;
}
