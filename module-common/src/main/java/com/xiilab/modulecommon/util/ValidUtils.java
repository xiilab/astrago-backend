package com.xiilab.modulecommon.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidUtils {
	public static boolean isNullOrZero(Integer value) {
		return value == null || value == 0;
	}

	public static boolean isNullOrZero(Long value) {
		return value == null || value == 0L;
	}

	public static boolean isNullOrZero(Float value) {
		return value == null || value == 0.0f;
	}

	public static boolean isNullOrFalse(Boolean value) { return value == null || !value; }
}
