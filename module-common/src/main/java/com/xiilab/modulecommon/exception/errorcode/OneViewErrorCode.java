package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OneViewErrorCode implements ErrorCode {
	NOT_FOUND_ONEVIEW_SETTING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "원뷰 설정 정보를 조회할 수 없습니다."),
	FAILED_SSL_VERIFICATION_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SSL 인증에 실패하였습니다. 관리자에게 문의해주세요."),
	DISCONNECTED_ONEVIEW_ACCOUNT(HttpStatus.SERVICE_UNAVAILABLE.value(), "원뷰 API 호출을 위해 계정 연동이 필요합니다.\n관리자에게 문의해주세요.")
	;
	private final int code;
	private final String message;
}
