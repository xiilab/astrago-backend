package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SmtpErrorCode implements ErrorCode {
	SMTP_INFO_MISS(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SMTP 정보가 유효하지 않습니다. 다시 확인해주세요."),
	SMTP_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SMTP 저장에 실패했습니다."),
	SMTP_DUPLICATION_USER_NAME(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 등록된 사용자 이름입니다. 다시 확인해주세요."),
	SMTP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 SMTP 존재하지 않습니다."),
	SMTP_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 SMTP 삭제에 실패했습니다."),
	SMTP_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 SMTP 변경에 실패했습니다."),
	SMTP_NOT_REGISTERED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 등록된 SMTP가 없습니다."),
	SMTP_NOT_FOUND_HOST_TYPE(HttpStatus.NOT_FOUND.value(), "해당 HOST_TYPE 없습니다."),
	;
	private final int code;
	private final String message;
}
