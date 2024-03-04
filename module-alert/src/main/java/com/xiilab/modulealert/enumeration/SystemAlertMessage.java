package com.xiilab.modulealert.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemAlertMessage {
	// 워크스페이스
	WORKSPACE_START("워크스페이스(%s)이(가) 시작되었습니다."),
	WORKSPACE_END("워크스페이스(%s)이(가) 종료되었습니다."),
	WORKSPACE_ERROR("워크스페이스(%s) 에러 발생했습니다."),
	// 리소스
	RESOURCE_APPROVAL("리소스 승인되었습니다."),
	// NODE
	NODE_ERROR("%s에 장애가 발생하였습니다.")
	;
	private final String message;
}
