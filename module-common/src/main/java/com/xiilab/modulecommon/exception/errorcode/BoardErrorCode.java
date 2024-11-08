package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
	NOT_FOUND_BOARD(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당하는 게시글을 찾을 수 없습니다."),
	NOT_FOUND_BOARDS(HttpStatus.INTERNAL_SERVER_ERROR.value(), "삭제할 수 없는 게시글이 포함되어 삭제할 수 없습니다."),
	NOT_FOUND_CONTENTS_FILE(HttpStatus.NOT_FOUND.value(), "해당하는 게시글에 포함된 이미지를 찾을 수 없습니다."),
	NOT_FOUND_ATTACHED_FILE(HttpStatus.NOT_FOUND.value(), "해당하는 게시글에 첨부된 파일을 찾을 수 없습니다."),
	FAILED_DOWNLOAD_ATTACHED_FILE(HttpStatus.NOT_FOUND.value(), "해당하는 게시글에 첨부된 파일을 다운로드 받을 수 없습니다.")
	;
	private final int code;
	private final String message;
}
