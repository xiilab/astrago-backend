package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemAlertErrorCode implements ErrorCode  {
	NOT_FOUND_SYSTEM_ALERT(HttpStatus.NOT_FOUND.value(), "해당 시스템 알람 상세정보를 조회할 수 없습니다."),
	ALERT_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "해당 알림에 접근 권한이 없습니다."),
	ALERT_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알림 발송에 실패했습니다."),
	NOT_FOUND_ALERT(HttpStatus.NOT_FOUND.value(), "해당 알림이 존재하지 않습니다.")
	;


	private final int code;
	private final String message;
}
