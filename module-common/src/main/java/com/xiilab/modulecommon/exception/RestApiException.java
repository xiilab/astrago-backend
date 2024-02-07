package com.xiilab.modulecommon.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class RestApiException extends RuntimeException{
	private final ErrorCode errorCode;
}
