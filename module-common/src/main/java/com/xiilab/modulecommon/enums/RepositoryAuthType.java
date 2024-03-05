package com.xiilab.modulecommon.enums;

import lombok.Getter;

@Getter
public enum RepositoryAuthType {
	PRIVATE("private"),
	PUBLIC("public");

	RepositoryAuthType(String type) {
		this.type = type;
	}

	private final String type;
}
