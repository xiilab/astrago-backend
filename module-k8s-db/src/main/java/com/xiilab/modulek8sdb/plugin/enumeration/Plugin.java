package com.xiilab.modulek8sdb.plugin.enumeration;

import lombok.Getter;

@Getter
public enum Plugin {
	DELL("DELL_UNITY", "2.11.0")
	;

	private String name;
	private String version;

	Plugin(String name, String version) {
		this.name = name;
		this.version = version;
	}
}
