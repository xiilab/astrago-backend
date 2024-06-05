package com.xiilab.modulek8sdb.smtp.enums;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SmtpErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HostType {
	GMAIL("smtp.gmail.com", 587),
	NAVER("smtp.naver.com", 465),
	DAUM("smtp.daum.net", 465),
	OUTLOOK("smtp.office365.com", 587),
	KAKAO("smtp.kakao.com", 465),

	;
	private String host;
	private int port;

	public static HostType getHostType(String host) {
		for (HostType type : HostType.values()) {
			if (type.getHost().equalsIgnoreCase(host)) {
				return type;
			}
		}
		throw new RestApiException(SmtpErrorCode.SMTP_NOT_FOUND_HOST_TYPE);
	}
}
