package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadErrorCode implements ErrorCode {
	FAILED_SAVE_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
	FAILED_READ_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 조회에 실패했습니다.")
	;
	private final int code;
	private final String message;
}
