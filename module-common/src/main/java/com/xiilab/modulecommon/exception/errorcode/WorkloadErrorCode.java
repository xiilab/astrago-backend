package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadErrorCode  implements ErrorCode {

	NOT_FOUND_BATCH_JOB_LOG(HttpStatus.NOT_FOUND.value(), "해당하는 배치 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_INTERACTIVE_JOB_LOG(HttpStatus.NOT_FOUND.value(), "해당하는 인터렉티브 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_WORKLOAD(HttpStatus.NOT_FOUND.value(), "해당하는 워크로드를 찾을 수 없습니다."),
	NOT_FOUND_POD(HttpStatus.NOT_FOUND.value(), "파드를 조회할 수 없습니다."),
	WORKLOAD_MESSAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 메시지가 전달되었습니다."),
	WORKLOAD_PIN_DUPLICATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"해당 워크로드는 이미 PIN이 추가되었습니다."),
	WORKLOAD_OPTIMIZATION_HOUR_INPUT_ERROR(HttpStatus.BAD_REQUEST.value(), "워크로드 최적화 정책 생성은 2시간 이상부터 가능합니다.")
	;

	private final int code;
	private final String message;
}
