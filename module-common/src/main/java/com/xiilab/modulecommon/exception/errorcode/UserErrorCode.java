package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	USER_CREATE_FAIL(HttpStatus.CONFLICT.value(), "회원가입을 실패했습니다."),
	USER_CREATE_FAIL_SAME_NAME(HttpStatus.CONFLICT.value(), "사용자 생성 실패, UserName 중복"),
	USER_CREATE_FAIL_SAME_EMAIL(HttpStatus.CONFLICT.value(), "사용자 생성 실패, EMAIL 중복"),
	USER_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND.value(), "해당 ID의 사용자가 없습니다."),
	USER_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND.value(), "해당 이름의 사용자가 없습니다."),
	USER_NOT_FOUND_INFO(HttpStatus.NOT_FOUND.value(), "일치하는 정보가 없습니다."),
	USER_AUTH_FAIL(HttpStatus.UNAUTHORIZED.value(), "해당 유저는 권한이 없습니다."),
	USER_WORKSPACES_CREATED_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY.value(), "이미 생성된 워크스페이스 개수가 수정하려는 개수보다 많아 수정할 수 없습니다.\n해당 사용자가 생성한 워크스페이스를 먼저 삭제해주세요."),
	WORKSPACE_CREATE_LIMIT_EXCEEDED(HttpStatus.UNPROCESSABLE_ENTITY.value(), "관리자가 설정한 워크스페이스 생성 제한 개수를 초과하여 생성할 수 없습니다.")
	;

	private final int code;
	private final String message;
}
