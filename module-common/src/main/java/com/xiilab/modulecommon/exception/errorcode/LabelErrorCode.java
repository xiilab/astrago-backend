package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LabelErrorCode implements ErrorCode {
	LABEL_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "라벨 등록에 실패하였습니다."),
	LABEL_DUPLICATE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 이름의 라벨이 이미 존재합니다."),
	LABEL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 라벨이 없습니다."),
	LABEL_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ID의 라벨 삭제 실패하였습니다."),

	;
	private final int code;
	private final String message;
}
