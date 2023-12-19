package com.xiilab.moduleuser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupCategory {
	ACCOUNT("account"),
	WORKSPACE("ws");

	private final String value;
}
