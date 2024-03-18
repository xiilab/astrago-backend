package com.xiilab.modulek8s.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
	public static LocalDateTime convertK8sUtcTimeString(String utcTimeStr) {
		// 문자열을 ZonedDateTime으로 파싱
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(utcTimeStr);
		// 서울 시간대로 변환
		ZonedDateTime seoulTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
		// 서울 시간대의 LocalDateTime으로 변환
		return seoulTime.toLocalDateTime();
	}
}
