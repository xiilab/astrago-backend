package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {

	REPORT_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(),"리포트 예약 삭제 실패하였습니다."),
	REPORT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 리포트 예약이 없습니다"),
	REPORT_SAVE_FAIL_OVER_CYCLE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "주기가 너무 큽니다."),
	REPORT_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "리포트 예약 실패하였습니다."),

	;

	private final int code;
	private final String message;
}
