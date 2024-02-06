package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadErrorCode  implements ErrorCode {

	NOT_FOUND_BATCH_JOB(HttpStatus.NOT_FOUND.value(), "해당하는 배치 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_INTERACTIVE_JOB(HttpStatus.NOT_FOUND.value(), "해당하는 인터렉티브 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_WORKLOAD(HttpStatus.NOT_FOUND.value(), "해당하는 워크로드를 찾을 수 없습니다."),
	NOT_FOUND_POD(HttpStatus.NOT_FOUND.value(), "파드를 조회할 수 없습니다."),
	WORKLOAD_MESSAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 메시지가 전달되었습니다."),
	;

	private final int code;
	private final String message;
}
