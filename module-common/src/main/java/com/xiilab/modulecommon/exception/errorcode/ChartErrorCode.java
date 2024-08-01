package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChartErrorCode implements ErrorCode {
	CHART_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 chart가 없습니다."),
	CHART_PANEL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 chart panel이 없습니다."),
	UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED.value(), "해당 유저는 권한이 없습니다.");
	private final int code;
	private final String message;
}
