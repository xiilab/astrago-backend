package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegistryErrorCode implements ErrorCode {
	PROJECT_CONFLICT(HttpStatus.CONFLICT.value(), "중복된 이름의 프로젝트가 존재합니다."),
	REGISTRY_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 정보를 입력하였습니다. 입력한 정보를 재확인해주세요"),
	REGISTRY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "권한이 존재하지 않거나, 존재하지 않습니다."),
	REGISTRY_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 이슈가 발생 할 경우 관리자에게 문의해주세요."),
	WORKLOAD_NOT_SUPPORT_ERROR(HttpStatus.BAD_REQUEST.value(), "해당 워크로드 타입은 이미지 커밋을 지원하지 않습니다."),
	;
	private final int code;
	private final String message;
}
