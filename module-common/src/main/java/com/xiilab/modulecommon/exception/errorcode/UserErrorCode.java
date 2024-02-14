package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	USER_CREATE_FAIL_SAME_NAME(HttpStatus.CONFLICT.value(), "사용자 생성 실패, UserName 중복"),
	USER_CREATE_FAIL_SAME_EMAIL(HttpStatus.CONFLICT.value(), "사용자 생성 실패, EMAIL 중복"),
	USER_CREATE_FAIL(HttpStatus.CONFLICT.value(), "사용자 생성 실패 하였습니다."),
	USER_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND.value(), "해당 ID의 사용자가 없습니다."),
	USER_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND.value(), "해당 이름의 사용자가 없습니다."),
	USER_NOT_FOUND_INFO(HttpStatus.NOT_FOUND.value(), "일치하는 정보가 없습니다."),

	;

	private final int code;
	private final String message;
}