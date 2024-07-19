package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourcePresetErrorCode implements ErrorCode {
	NOT_FOUND_RESOURCE_PRESET(HttpStatus.INTERNAL_SERVER_ERROR.value(), "리소스 프리셋 상세정보가 존재하지 않습니다.")
	;

	private final int code;
	private final String message;
}
