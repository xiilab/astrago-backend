package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NetworkErrorCode implements ErrorCode {
	NETWORK_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "네트워크 폐쇄망 설정 정보가 존재하지 않습니다."),
	;
	private final int code;
	private final String message;
}
