package com.xiilab.moduleuser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAttribute {
	SIGN_UP_PATH("signUpPath"),
	WORKSPACE_CREATE_LIMIT("workspaceCreateLimit"),
	APPROVAL_YN("approvalYN")
	;

	private final String key;
}
