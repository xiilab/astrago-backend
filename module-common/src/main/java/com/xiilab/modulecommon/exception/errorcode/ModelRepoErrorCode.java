package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelRepoErrorCode implements ErrorCode {
	MODEL_REPO_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "모델 등록에 실패하였습니다."),
	MODEL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 모델이 없습니다."),

	;
	private final int code;
	private final String message;
}
