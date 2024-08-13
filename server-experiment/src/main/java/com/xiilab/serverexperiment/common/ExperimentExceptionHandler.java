package com.xiilab.serverexperiment.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.ErrorCode;
import com.xiilab.modulecommon.exception.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExperimentExceptionHandler {
	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ErrorResponse> handleException(CommonException e) {
		ErrorCode errorCode = e.getErrorCode();
		String msg = String.format(errorCode.getMessage());
		return handleExceptionInternal(errorCode, msg);
	}

	private ResponseEntity<ErrorResponse> handleExceptionInternal(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode, message));
	}

	private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(message)
			.build();
	}
}
