package com.xiilab.modulecommon.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DataConverter {
	private final String dateFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * DateTime 포멧하는 메소드
	 * @param unixTime  Prometheus에서 조회된 UnixTime
	 * @return 포멧된 DateTime
	 */
	public String formatDateTime(double unixTime) {
		// UnixTime LocalDateTime으로 변환
		LocalDateTime dateTime = Instant.ofEpochSecond((long)unixTime)
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return dateTime.format(formatter);
	}

	public String formatDateTime(String dateTime) {
		// 입력된 문자열을 LocalDateTime으로 변환
		LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat); // 원하는 출력 형식 지정

		// 지정된 포맷 형식으로 변환하여 문자열 반환
		return parsedDateTime.format(formatter);
	}

	/**
	 * JsonNode로부터 필드 값을 가져오거나 Null을 반환하는 메서드입니다.
	 *
	 * @param node       JsonNode
	 * @param fieldName  필드 이름
	 * @return 가져온 필드 값 또는 Null
	 */
	public String getStringOrNull(JsonNode node, String fieldName) {
		JsonNode field = node.get(fieldName);
		return field == null ? "" : field.asText();
	}

	/**
	 * DateTime UnixTime으로 변환하는 메소드
	 * @param formattedDateTime 변환될 Date Time
	 * @return 변경된 UnixTime
	 */
	public String toUnixTime(String formattedDateTime) {
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, DateTimeFormatter.ofPattern(dateFormat));
		return String.valueOf(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
	}
	/**
	 * data size 변환 메소드
	 * @param sizeStr 변환될 dataSize
	 * @return 변환된 dataSize
	 */
	public String formatSize(String sizeStr) {
		long size = Long.parseLong(sizeStr);

		if (size >= 1000000000000L) { // 1 TB
			return String.format("%.2fTB", (double)size / 1000000000000L);
		} else if (size >= 1000000000L) { // 1 GB
			return String.format("%.2fGB", (double)size / 1000000000L);
		} else if (size >= 1000000L) { // 1 MB
			return String.format("%.2fMB", (double)size / 1000000L);
		} else {
			return String.format("%dbytes", size);
		}
	}
}
