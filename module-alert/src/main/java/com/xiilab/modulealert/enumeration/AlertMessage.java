package com.xiilab.modulealert.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertMessage {
	// 워크스페이스
	WORKSPACE_START("워크스페이스(%s)이(가) 시작되었습니다."),
	WORKSPACE_END("워크스페이스(%s)이(가) 종료되었습니다."),
	WORKSPACE_ERROR("워크스페이스(%s) 에러 발생했습니다."),
	// 리소스
	RESOURCE_APPROVAL("리소스 승인되었습니다."),
	RESOURCE_OPTIMIZATION_ALERT("워크스페이스(%s)에 속한 워크로드(%s)가 관리자가 지정한 자원정책에 따라 사용량이 낮아 삭제 될 예정입니다."),
	RESOURCE_OPTIMIZATION_RESULT("워크스페이스(%s)에 속한 워크로드(%s)가 관리자가 지정한 자원정책에 따라 삭제 되었습니다.")
	;
	private final String message;
}
