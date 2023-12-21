package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;

@Getter
public enum CodeRepositoryType {
	PRIVATE("private"),
	PUBLIC("public");

	CodeRepositoryType(String type) {
		this.type = type;
	}

	private final String type;
}
