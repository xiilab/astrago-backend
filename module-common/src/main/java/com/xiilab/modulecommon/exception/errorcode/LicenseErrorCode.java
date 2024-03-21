package com.xiilab.modulecommon.exception.errorcode;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LicenseErrorCode implements ErrorCode {
	//저장된 라이센스 키가 없음
	NOT_FOUND_LICENSE_KEYS(444, "There are no saved license keys."),
	//라이센스 키 인증 안됨
	INVALID_LICENSE_KEYS(445, "Invalid license key."),
	//라이센스 키 만료일 지남
	LICENSE_PAST_EXPIRATION_DATE(446, "License key past its expiration date.")
	;

	private final int status;
	private final String message;

	@Override
	public int getCode() {
		return this.status;
	}
}
