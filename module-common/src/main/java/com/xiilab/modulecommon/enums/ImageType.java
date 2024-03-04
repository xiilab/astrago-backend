package com.xiilab.modulecommon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
	BUILT("built-in-image"),
	HUB("hub"),
	CUSTOM("custom");

	private final String type;
}
