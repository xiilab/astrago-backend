package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;

@Getter
public enum ReclaimPolicyType {
	DELETE("Delete"),
	RETAIN("Retain");

	private String field;

	ReclaimPolicyType(String field) {
		this.field = field;
	}
}
