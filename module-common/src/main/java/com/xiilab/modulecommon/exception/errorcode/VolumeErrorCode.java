package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VolumeErrorCode implements ErrorCode {


	VOLUME_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 볼륨이 존재하지 않습니다."),
	VOLUME_FORBIDDEN_MODIFY(HttpStatus.FORBIDDEN.value(), "자신이 생성한 볼륨만 수정할 수 있습니다."),
	VOLUME_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN.value(), "자신이 생성한 볼륨만 삭제할 수 있습니다."),
	VOLUME_NOT_DELETE_IN_USE(HttpStatus.FORBIDDEN.value(), "사용중인 볼륨은 삭제할 수 없습니다."),

	;


	private final int code;
	private final String message;
}

