package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TusErrorCode implements ErrorCode {
	FILE_NAME_ERROR_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "저장하려는 원본 파일이 손상되어 업로드에 실패했습니다."),
	UPLOAD_FAILED_MESSAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "저장하려는 원본 파일이 손상되어 업로드에 실패했습니다.");

	private final int code;
	private final String message;
}
