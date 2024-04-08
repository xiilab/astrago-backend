package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkloadErrorCode implements ErrorCode {
	NOT_FOUND_WORKLOAD_POD(HttpStatus.NOT_FOUND.value(), "해당하는 워크로드의 pod를 조회 할 수 없습니다. 해당 워크로드가 종료되었거나, 실행되지 않았는지 확인바랍니다."),
	NOT_FOUND_JOB_LOG(HttpStatus.NOT_FOUND.value(), "해당 하는 로그를 조회 할 수 없습니다."),
	NOT_FOUND_BATCH_JOB_LOG(HttpStatus.NOT_FOUND.value(), "해당하는 배치 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_INTERACTIVE_JOB_LOG(HttpStatus.NOT_FOUND.value(), "해당하는 인터렉티브 잡 로그를 조회할 수 없습니다."),
	NOT_FOUND_WORKLOAD(HttpStatus.NOT_FOUND.value(), "해당하는 워크로드를 찾을 수 없습니다."),
	NOT_FOUND_POD(HttpStatus.NOT_FOUND.value(), "파드를 조회할 수 없습니다."),
	WORKLOAD_MESSAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 메시지가 전달되었습니다."),
	WORKLOAD_PIN_DUPLICATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"해당 워크로드는 이미 PIN이 추가되었습니다."),
	WORKLOAD_FOLDER_DOWN_ERR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"워크로드 폴더 다운로드는 아직 지원하지 않습니다."),
	FAILED_CREATE_WORKLOAD(HttpStatus.INTERNAL_SERVER_ERROR.value(),"워크로드 생성에 실패하였습니다."),
	FAILED_SAVE_WORKLOAD_HISTORY(HttpStatus.INTERNAL_SERVER_ERROR.value(),"워크로드 히스토리 등록에 실패하였습니다."),
	FAILED_LOAD_WORKLOAD_INFO(HttpStatus.INTERNAL_SERVER_ERROR.value(),"해당 이름의 워크로드가 존재하지 않습니다."),
	DELETED_WORKLOAD_INFO(HttpStatus.INTERNAL_SERVER_ERROR.value(),"삭제된 워크로드는 조회할 수 없습니다."),
	FAILED_UPDATE_END_WORKLOAD_INFO(HttpStatus.INTERNAL_SERVER_ERROR.value(),"워크로드 히스토리 수정에 실패하였습니다."),
	FAILED_LOAD_CODE_CREDENTIAL_INFO(HttpStatus.INTERNAL_SERVER_ERROR.value(),"코드의 크레덴셜 정보 조회에 실패하였습니다."),
	FAILED_LOAD_IMAGE_INFO(HttpStatus.INTERNAL_SERVER_ERROR.value(),"워크로드의 이미지 정보를 조회할 수 없습니다."),
	WORKLOAD_OPTIMIZATION_HOUR_INPUT_ERROR(HttpStatus.BAD_REQUEST.value(), "워크로드 최적화 정책 생성은 5시간 이상, 24시간 이하만 가능합니다.")
	;

	private final int code;
	private final String message;
}
