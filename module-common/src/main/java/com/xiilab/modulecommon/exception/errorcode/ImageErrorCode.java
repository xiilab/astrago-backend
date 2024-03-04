package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
	NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND.value(), "해당하는 이미지를 조회할 수 없습니다."),
	NOT_FOUND_CREDENTIAL(HttpStatus.NOT_FOUND.value(), "해당하는 크레덴셜을 조회할 수 없습니다.");

	private final int code;
	private final String message;
}
