package com.xiilab.servercore.config;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.config.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST.value(), "global.exception.badRequest"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "global.exception.notFound"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "global.exception.internalServerError");

	private final int code;
	private final String message;
}
