package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubErrorCode implements ErrorCode {
	FAILED_ENV_MAP_TO_JSON(HttpStatus.INTERNAL_SERVER_ERROR.value(), "환경변수를 JSON 문자열로 변환하는데 실패했습니다."),
	GET_FAILED_HUB_REAM_ME(HttpStatus.INTERNAL_SERVER_ERROR.value(), "허브 상세 정보 조회를 실패했습니다.")
	;
	private final int code;
	private final String message;
}
