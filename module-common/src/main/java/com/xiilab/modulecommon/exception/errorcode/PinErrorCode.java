package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PinErrorCode implements ErrorCode {
	PIN_ADD_ERROR_MESSAGE(HttpStatus.BAD_REQUEST.value(), "pin은 6개 이상 등록할 수 없습니다."),
	;
	private final int code;
	private final String message;
}
