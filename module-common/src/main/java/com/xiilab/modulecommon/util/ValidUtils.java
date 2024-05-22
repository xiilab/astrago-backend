package com.xiilab.modulecommon.util;

import java.util.Arrays;

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

	// 파일이 Mac 시스템에서 생성된 파일인지 검사
	public static boolean isCheckExtensionForMac(String fileName) {
		String[] macFileExtensions = new String[]{".DS_Store", "__MACOSX"};
		return Arrays.stream(macFileExtensions).anyMatch(fileName::contains);
	}
}
