package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAttribute {
	SIGN_UP_PATH("signUpPath"),
	WORKSPACE_CREATE_LIMIT("workspaceCreateLimit"),
	APPROVAL_YN("approvalYN"),
	USER_UUID("user_uuid") // 모비젠 데이터 포털 전용 UUID
	;

	private final String key;
}
