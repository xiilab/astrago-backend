package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelErrorCode implements ErrorCode {

	MODEL_FIX_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "모델 수정 권한이 없습니다."),
	MODEL_NOT_DELETE_IN_USE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용중인 모델은 삭제할 수 없습니다."),
	MODEL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "모델이 존재하지 않습니다."),
	MODEL_ZIP_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "디렉토리 압축 및 다운로드를 실패했습니다."),
	MODEL_FILE_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 다운로드를 실패했습니다."),
	MODEL_FILE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일이 존재하지 않습니다."),
	MODEL_DIRECTORY_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "폴더 생성을 실패했습니다."),
	MODEL_DIRECTORY_CREATE_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 생성된 폴더입니다."),
	MODEL_WORKSPACE_MAPPING_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 추가된 모델입니다."),
	MODEL_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "모델 삭제 권한이 없습니다."),
	MODEL_NOT_SUPPORT_PREVIEW(HttpStatus.INTERNAL_SERVER_ERROR.value(), "미리보기를 지원하지 않는 포맷입니다."),
	MODEL_PREVIEW_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 미리보기를 실패했습니다."),
	;
	private final int code;
	private final String message;
}
