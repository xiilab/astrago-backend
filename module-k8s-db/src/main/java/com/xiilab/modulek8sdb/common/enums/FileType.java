package com.xiilab.modulek8sdb.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {
	D("directory"),
	F("file")
	;
	private final String type;
}
