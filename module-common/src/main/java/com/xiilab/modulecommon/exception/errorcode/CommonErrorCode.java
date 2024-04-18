package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	// global
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST.value(), "global.exception.badRequest"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "global.exception.notFound"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "global.exception.internalServerError"),
	// data
	NUMBER_ROUND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "반올림 실패하였습니다."),
	DATA_FORMAT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터 포멧 실패하였습니다."),
	MONTH_WEEKS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST.value(), "해당 월은 입력한 주차가 없습니다."),
	// file
	PLUGIN_ALREADY_INSTALLED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미 설치된 플러그인입니다."),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "파일이 존재하지 않습니다."),
	FILE_PERMISSION_DENIED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "%s에 권한이 없어 삭제를 실패했습니다."),
	FILE_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 저장에 실패했습니다."),
	FILE_INFO_LOOKUP_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 정보 불러오기에 실패했습니다."),
	FILE_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 다운로드에 실패했습니다."),
	FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 업로드를 실패했습니다."),
	// alert
	ALERT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 알림이 존재하지 않습니다."),
	ALERT_NOT_FOUND_WORKSPACE_NAME(HttpStatus.NOT_FOUND.value(), "해당 워크스페이스 알림 설정이 존재하지 않습니다."),
	ALERT_SET_UPDATE_FAIL(HttpStatus.NOT_FOUND.value(), "해당 워크스페이스 알림 설정 수정 실패했습니다."),
	ALERT_SET_SAVE_FAIL(HttpStatus.NOT_FOUND.value(), "해당 워크스페이스 알림 설정 저장 실패했습니다."),
	ALERT_SET_DELETE_FAIL(HttpStatus.UNAUTHORIZED.value(), "해당 워크스페이스 알림 설정 삭제 실패했습니다."),
	ALERT_MANAGER_NOTFOUND_ROLE(HttpStatus.NOT_FOUND.value(), "PrometheusRule에 사용될 Rule이 없습니다."),
	ALERT_MANAGER_ADD_RULE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PrometheusRule 추가 실패하였습니다."),
	ALERT_MANAGER_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ALERT MANAGER 삭제 실패하였습니다."),
	ALERT_MANAGER_K8S_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PrometheusRule 삭제 실패하였습니다."),
	ALERT_MANAGER_NOTFOUND(HttpStatus.NOT_FOUND.value(), "해당 ID의 Alert Manager 없습니다."),
	ALERT_MANAGER_EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Email, System 수신 선택시 사용자 정보값은 필수입니다."),
	ALERT_MANAGER_JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ALERT MANAGER JSON PARSE ERROR"),
	ALERT_MANAGER_RULE_READY(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 ALERT은 이미 사용중입니다."),
	ALERT_MANAGER_TIME_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 시간의 알림이 존재하지 않습니다."),
	// HUB
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "허브 상세정보가 존재하지 않습니다."),
	// terminal
	TERMINAL_CLOSE_ERROR(HttpStatus.INSUFFICIENT_STORAGE.value(), "web terminal close error"),
	// monitor
	MONITOR_METRIC_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 메트릭이 존재하지 않습니다."),
	// credential
	CREDENTIAL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 크레덴셜이 존재하지 않습니다."),
	IMAGE_CREDENTIAL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "이미지 크레덴셜이 존재하지 않습니다."),
	// Mail
	MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "메일 발송을 실패하였습니다."),
	FAILED_CONNECT_GITLAB(HttpStatus.INTERNAL_SERVER_ERROR.value(), "GitLab API 연결에 실패하였습니다."),
	FAILED_SEARCH_GITLAB_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당하는 프로젝트가 GitLab에 존재하지 않습니다."),
	FAILED_UTC_TO_KOR_TIME(HttpStatus.INTERNAL_SERVER_ERROR.value(), "국제표준시(UTC)를 한국 시간으로 변환하는데 실패하였습니다."),
	;
	private final int code;
	private final String message;
}
