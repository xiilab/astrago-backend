package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UtilsErrorCode implements ErrorCode {
	NOT_SUPPORT_COMPRESS_TYPE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "지원하지 않는 압축형식입니다."),
	NO_SELECTED_COMPRESS_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "압축할 파일이 선택되지 않았습니다."),
	NO_SELECTED_DECOMPRESS_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "압축 해제할 파일이 선택되지 않았습니다."),
	NOT_FOUND_COMPRESS_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "입력한 경로의 파일 또는 디렉토리가 존재하지 않습니다."),
	IS_NOT_FOLDER(HttpStatus.INTERNAL_SERVER_ERROR.value(), "폴더만 압축할 수 있습니다."),
	FAILED_COMPRESS_ADD_DIR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "폴더를 추가하는 중에 오류가 발생했습니다."),
	FAILED_COMPRESS_ADD_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일을 추가하는 중에 오류가 발생했습니다."),
	FAILED_COMPRESS_ZIP_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ZIP파일 압축에 실패했습니다."),
	FAILED_COMPRESS_TAR_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "TAR파일 압축에 실패했습니다."),
	FAILED_DECOMPRESS_ZIP_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ZIP파일 압축 해제에 실패했습니다."),
	FAILED_DECOMPRESS_TAR_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "TAR파일 압축 해제에 실패했습니다."),
	FAILED_DECOMPRESS_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 압축 해제를 하는데 실패했습니다."),
	UNKNOWN_DECOMPRESS_FILE_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 압축해제에 실패했습니다.\n압축 해제를 지원하지 않는 포맷입니다."),
	;

	private final int code;
	private final String message;
}
