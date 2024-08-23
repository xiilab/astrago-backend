package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeployErrorCode implements ErrorCode {

	NOT_FOUND_MODEL_VERSION(HttpStatus.NOT_FOUND.value(), "해당 버전의 모델이 존재하지 않습니다."),
	FAILED_CREATE_DEPLOY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서비스 생성을 실패했습니다."),
	;
	private final int code;
	private final String message;
}
