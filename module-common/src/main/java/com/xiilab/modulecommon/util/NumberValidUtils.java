package com.xiilab.modulecommon.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NumberValidUtils {
	public static boolean isNullOrZero(Integer value) {
		return value == null || value == 0;
	}

	public static boolean isNullOrZero(Long value) {
		return value == null || value == 0L;
	}
}
