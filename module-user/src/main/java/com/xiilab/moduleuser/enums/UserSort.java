package com.xiilab.moduleuser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSort {
	CREATED_AT_ASC,
	CREATED_AT_DESC,
	ENABLE_ASC,
	ENABLE_DESC,
	ROLE_ADMIN,
	ROLE_USER,
}
