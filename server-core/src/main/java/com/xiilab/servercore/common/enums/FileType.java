package com.xiilab.servercore.common.enums;

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
