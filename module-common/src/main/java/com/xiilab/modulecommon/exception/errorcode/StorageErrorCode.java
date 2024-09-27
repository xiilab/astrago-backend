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
	FAILD_DELETE_USING_STORAGE(HttpStatus.METHOD_NOT_ALLOWED.value(), "해당 스토리지를 사용중인 볼륨이 존재해 삭제할 수 없습니다."),
	STORAGE_ALREADY_INSTALLED_DELL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Dell 플러그인이 이미 설치되어 있습니다"),
	STORAGE_INSTALL_WAIT(HttpStatus.INTERNAL_SERVER_ERROR.value(),"DELL 플러그인 설치 진행중입니다. 잠시만 기다려주세요."),
	DELL_STORAGE_ARRAY_ID_NULL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "DELL 스토리지 Array ID를 입력해주세요"),
	DELL_STORAGE_VOLUME_ID_NULL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "DELL 스토리지 STORAGE VOLUME ID를 입력해주세요."),
	DELL_STORAGE_VOLUME_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 이름의 DELL 스토리지 없습니다."),
	STORAGE_USAGE_DELETE_FAIL(HttpStatus.SERVICE_UNAVAILABLE.value(), "해당 플러그인의 스토리지가 사용 중이므로 삭제할 수 없습니다.");
	;


	private final int code;
	private final String message;
}
