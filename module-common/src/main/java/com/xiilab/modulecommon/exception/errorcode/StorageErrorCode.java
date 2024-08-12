package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCode {

	STORAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 스토리지 클래스가 존재하지 않습니다."),
	STORAGE_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "NFS 스토리지 연결 실패"),
	STORAGE_DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "스토리지 전용 디렉토리 생성을 실패했습니다."),
	STORAGE_ALREADY_INSTALLED_IBM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "IBM 스토리지가 이미 설치되어 있습니다."),
	STORAGE_INSTALL_FAIL_IBM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "IBM 스토리지가 설치 실패하였습니다."),
	STORAGE_AFTER_AGAIN_INSTALL_IBM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "IBM 스토리지 설치를 잠시 후 다시 해주세요."),
	STORAGE_RETRY_AGAIN_INSTALL_IBM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "IBM 스토리지 설치를 다시 해주세요."),
	STORAGE_UNINSTALL_FAIL_IBM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "IBM 스토리지가 삭제 실패하였습니다."),
	FAILD_DELETE_USING_STORAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 스토리지를 사용중인 데이터 셋, 모델이 존재해 삭제할 수 없습니다."),



	;


	private final int code;
	private final String message;
}
