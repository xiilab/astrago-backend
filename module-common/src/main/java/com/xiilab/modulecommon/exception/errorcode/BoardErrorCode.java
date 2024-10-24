package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
	NOT_FOUND_NOTICE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당하는 URL에 연결할 수 없습니다."),
	;
	private final int code;
	private final String message;
}
