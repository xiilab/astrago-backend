package com.xiilab.modulecommon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompressFileType {
	ZIP("zip"),
	TAR("tar")
	;

	private final String extension;
}
