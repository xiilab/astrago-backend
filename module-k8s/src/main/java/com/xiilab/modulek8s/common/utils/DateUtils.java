package com.xiilab.modulek8s.common.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
	public static LocalDateTime convertK8sUtcTimeString(String utcTimeStr) {
		// UTC 문자열을 ZonedDateTime 객체로 변환
		ZonedDateTime utcTime = ZonedDateTime.parse(utcTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
		// ZonedDateTime 객체를 LocalDateTime으로 변환
		return utcTime.toLocalDateTime();
	}
}
