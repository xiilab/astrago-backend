package com.xiilab.servercore.config;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xiilab.modulecommon.config.ErrorCode;
import com.xiilab.modulecommon.dto.ResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	private final CommonMessageUtils commonMessageUtils;

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseDTO> handleIllegalArgument(IllegalArgumentException e) {
		log.warn("handleIllegalArgument", e);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;

		return ResponseEntity
			.status(errorCode.getCode())
			.body(new ResponseDTO(
				errorCode.getCode(),
				LocalDateTime.now(),
				commonMessageUtils.getMessage(errorCode.getMessage()),
				null));
	}
}
