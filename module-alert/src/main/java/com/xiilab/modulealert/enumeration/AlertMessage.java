package com.xiilab.modulealert.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertMessage {
	CREATE_WORKSPACE("워크스페이스(%s)이(가) 생성되었습니다."),
	DELETE_WORKSPACE("워크스페이스(%s)이(가) 삭제되었습니다."),
	INVITE_WORKSPACE("워크스페이스(%s)이(가) 초대되었습니다."),
	CREATE_WORKLOAD("워크로드(%s)이(가) 생성되었습니다."),
	START_WORKLOAD("워크로드(%s)이(가) 시작되었습니다."),
	DELETE_WORKLOAD("워크로드(%s)이(가) 삭제되었습니다."),
	END_WORKLOAD("워크로드(%s)이(가) 종료되었습니다."),
	ERROR_WORKLOAD("워크로드(%s)이(가) 에러 발생했습니다."),
	APPROVAL_RESOURCE("리소스 승인되었습니다."),

	;
	private final String message;
}
