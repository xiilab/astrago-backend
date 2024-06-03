package com.xiilab.modulek8sdb.smtp.enums;

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
		throw new IllegalArgumentException("No HostType found for host: " + host);
	}
}
