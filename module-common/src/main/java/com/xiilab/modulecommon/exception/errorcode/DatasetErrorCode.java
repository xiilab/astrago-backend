package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DatasetErrorCode implements ErrorCode {

	DATASET_FIX_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "데이터 셋 수정 권한이 없습니다."),
	DATASET_NOT_DELETE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용중인 데이터 셋은 삭제할 수 없습니다."),
	DATASET_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "데이터 셋이 존재하지 않습니다."),
	;
	private final int code;
	private final String message;
}
