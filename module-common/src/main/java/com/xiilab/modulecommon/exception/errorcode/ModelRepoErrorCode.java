package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelRepoErrorCode implements ErrorCode {
	MODEL_REPO_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "모델 등록에 실패하였습니다."),
	MODEL_REPO_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 모델이 없습니다."),
	MODEL_REPO_VERSION_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 모델 버전이 없습니다."),
	MODEL_REPO_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 모델을 사용중인 서비스가 있습니다. 사용중인 모델은 삭제할 수 없습니다."),

	;
	private final int code;
	private final String message;
}
