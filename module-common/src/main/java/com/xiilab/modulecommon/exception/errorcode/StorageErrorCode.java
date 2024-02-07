package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCode {

	STORAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 스토리지 클래스가 존재하지 않습니다."),
	STORAGE_CONNECTION_FAILED(HttpStatus.NO_CONTENT.value(), "NFS 스토리지 연결 실패"),
	STORAGE_DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "스토리지 전용 디렉토리 생성을 실패했습니다."),
	;


	private final int code;
	private final String message;
}
