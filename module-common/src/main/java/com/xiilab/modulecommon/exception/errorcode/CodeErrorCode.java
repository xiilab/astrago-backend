package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CodeErrorCode implements ErrorCode {
	CONNECTION_ERROR_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당하는 URL에 연결할 수 없습니다."),
	CODE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 소스코드가 존재하지 않습니다."),
	CODE_INVALID_TOKEN_OR_URL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "유효하지 않은 URL 혹은 공유 레포지토리인경우 크레덴셜을 제거해주세요"),
	CODE_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "소스코드 저장에 실패하였습니다."),
	UNSUPPORTED_REPOSITORY_ERROR_CODE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "지원하지 않는 소스코드 URL입니다."),
	NOT_FOUND_BRANCH_LIST(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 소스코드 URL이거나, 접근 권한이 없는 소스코드입니다."),
	FAILED_SAVE_USER_CODE(HttpStatus.NOT_FOUND.value(), "사용자 코드 저장에 실패하였습니다."),
	CODE_EXIST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 등록된 소스코드 입니다."),
	CODE_INPUT_ERROR(HttpStatus.BAD_REQUEST.value(), "입력 데이터를 다시 확인해주세요"),
	CODE_GET_BLANCHES_FAIL(HttpStatus.BAD_REQUEST.value(), "해당 코드의 브랜치를 가져오는데 실패했습니다. 입력 데이터를 다시 확인해주세요"),
	FAILED_JSON_TO_MAP(HttpStatus.INTERNAL_SERVER_ERROR.value(), "저장된 ARGUMENT를 변환하는데 실패하였습니다."),
	;
	private final int code;
	private final String message;
}
