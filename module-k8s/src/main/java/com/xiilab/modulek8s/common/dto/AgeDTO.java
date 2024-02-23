package com.xiilab.modulek8s.common.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgeDTO {
	private long days;
	private long hour;
	private long minutes;

	/**
	 * 생성자 패턴을 통한 경과시간 조회
	 *
	 * @param startTime 시작시간
	 */
	public AgeDTO(LocalDateTime startTime) {
		// Duration을 사용하여 시간 차이 계산
		Duration duration = Duration.between(startTime, LocalDateTime.now());
		days = duration.toDays();
		hour = duration.toHours() % 24;
		minutes = duration.toMinutes() % 60;
	}
	/**
	 * 생성자 패턴을 통한 경과시간 조회
	 *
	 * @param startTime 시작시간
	 */
	public AgeDTO(String creationTimestamp) {
		// ISO 8601 형식의 문자열을 ZonedDateTime으로 변환
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(creationTimestamp);

		// ZonedDateTime을 LocalDateTime으로 변환
		LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
		// Duration을 사용하여 시간 차이 계산
		Duration duration = Duration.between(localDateTime, LocalDateTime.now());
		days = duration.toDays();
		hour = duration.toHours() % 24;
		minutes = duration.toMinutes() % 60;
	}
}
