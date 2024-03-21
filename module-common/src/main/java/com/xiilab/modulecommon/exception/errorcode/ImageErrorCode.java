package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
	NOT_FOUND_IMAGE_PORT(HttpStatus.NOT_FOUND.value(), "이미지 포트를 조회할 수 없습니다."),
	NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND.value(), "해당하는 이미지를 조회할 수 없습니다."),
	NOT_FOUND_CREDENTIAL(HttpStatus.NOT_FOUND.value(), "해당하는 크레덴셜을 조회할 수 없습니다."),
	FAILED_SAVE_BUILT_IN_IMAGE(HttpStatus.NOT_FOUND.value(), "빌트인 이미지 저장에 실패하였습니다."),
	FAILED_SAVE_HUB_IMAGE(HttpStatus.NOT_FOUND.value(), "허브 이미지 저장에 실패하였습니다."),
	FAILED_SAVE_CUSTOM_IMAGE(HttpStatus.NOT_FOUND.value(), "커스텀 이미지 저장에 실패하였습니다.");

	private final int code;
	private final String message;
}
