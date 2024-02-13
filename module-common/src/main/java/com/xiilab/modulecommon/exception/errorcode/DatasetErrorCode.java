package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DatasetErrorCode implements ErrorCode {

	DATASET_FIX_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "데이터 셋 수정 권한이 없습니다."),
	DATASET_NOT_DELETE_IN_USE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용중인 데이터 셋은 삭제할 수 없습니다."),
	DATASET_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "데이터 셋이 존재하지 않습니다."),
	DATASET_ZIP_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "디렉토리 압축 및 다운로드를 실패했습니다."),
	DATASET_FILE_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 다운로드를 실패했습니다."),
	DATASET_FILE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일이 존재하지 않습니다."),
	DATASET_DIRECTORY_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "폴더 생성을 실패했습니다."),
	DATASET_DIRECTORY_CREATE_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 생성된 폴더입니다."),
	DATASET_WORKSPACE_MAPPING_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 추가된 데이터 셋입니다."),
	DATASET_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "데이터 셋 삭제 권한이 없습니다."),
	DATASET_NOT_SUPPORT_PREVIEW(HttpStatus.INTERNAL_SERVER_ERROR.value(), "미리보기를 지원하지 않는 포맷입니다."),
	DATASET_PREVIEW_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 미리보기를 실패했습니다."),
	;
	private final int code;
	private final String message;
}
