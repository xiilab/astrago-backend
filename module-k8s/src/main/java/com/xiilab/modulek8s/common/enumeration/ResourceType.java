package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;

@Getter
public enum ResourceType {
	WORKSPACE("ws"),
	WORKLOAD("wl"),
	VOLUME("vo"),
	STORAGE("st"),
	PLUGIN("pg"),
	SERVICE("sv");

	private String name;

	ResourceType(String name) {
		this.name = name;
	}
}
