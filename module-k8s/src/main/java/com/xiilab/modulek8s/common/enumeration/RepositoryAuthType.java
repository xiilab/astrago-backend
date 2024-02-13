package com.xiilab.modulek8s.common.enumeration;

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
