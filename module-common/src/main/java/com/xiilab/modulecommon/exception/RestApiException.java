package com.xiilab.modulecommon.exception;

import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class RestApiException extends RuntimeException{
	private final ErrorCode errorCode;
	private final String target;

	public RestApiException(ErrorCode errorCode, String target) {
		this.errorCode = errorCode;
		this.target = target;
	}
	public RestApiException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.target = "";
	}
}
