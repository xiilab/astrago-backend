package com.xiilab.modulek8s.node.enumeration;

import lombok.Getter;

@Getter
public enum MPSCapable {
	TRUE("true"),
	FALSE("false");

	private final String name;

	MPSCapable(String name) {
		this.name = name;
	}
}
