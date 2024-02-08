package com.xiilab.modulecommon.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

@Service
public class DataConverterUtil {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * DateTime 포멧하는 메소드
	 * @param unixTime  Prometheus에서 조회된 UnixTime
	 * @return 포멧된 DateTime
	 */
	public static String formatDateTime(double unixTime) {
		// UnixTime LocalDateTime으로 변환
		LocalDateTime dateTime = Instant.ofEpochSecond((long)unixTime)
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return dateTime.format(formatter);
	}

	/**
	 * JsonNode로부터 필드 값을 가져오거나 Null을 반환하는 메서드입니다.
	 *
	 * @param node       JsonNode
	 * @param fieldName  필드 이름
	 * @return 가져온 필드 값 또는 Null
	 */
	public static String getStringOrNull(JsonNode node, String fieldName) {
		JsonNode field = node.get(fieldName);
		return field == null ? "" : field.asText();
	}

	/**
	 * DateTime UnixTime으로 변환하는 메소드
	 * @param formattedDateTime 변환될 Date Time
	 * @return 변경된 UnixTime
	 */
	public static String toUnixTime(String formattedDateTime) {
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, DateTimeFormatter.ofPattern(dateFormat));
		return String.valueOf(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
	}

	/**
	 * data 소수점 두번째 자리 반올림 메소드
	 * @param sizeStr 반올림될 값
	 * @return 변환된 값
	 */
	public static double formatRoundTo(String sizeStr) {
		try {
			// 문자열을 double로 변환
			double inputValue = Double.parseDouble(sizeStr);

			// 소수점 두 번째 자리까지 반올림
			return Math.round(inputValue * 100.0) / 100.0;
		} catch (NumberFormatException e) {
			throw new CommonException(CommonErrorCode.NUMBER_ROUND_FAIL);
		}
	}
	public static JsonNode jsonparser(String jsonResponse) throws JsonProcessingException {
		return objectMapper.readTree(jsonResponse);
	}
	/**
	 * Prometheus에서 조회된 Metric objectMapper 매핑하는 메소드
	 * @param metric 매핑될 metric
	 * @return 매핑된 값
	 */
	public static String formatObjectMapper(String metric){
		try{
			return objectMapper.readTree(metric).get("data").get("result").elements().next().get("value").get(1).asText();
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonErrorCode.DATA_FORMAT_FAIL);
		}
	}
	/**
	 * Prometheus에서 조회된 Metric JsonNode 매핑하는 메소드
	 * @param metric 매핑될 metric
	 * @return 매핑된 값
	 */
	public static Iterator<JsonNode> formatJsonNode(String metric){
		try{
			return objectMapper.readTree(metric).get("data").get("result").elements();
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonErrorCode.DATA_FORMAT_FAIL);
		}
	}
	public static String getRepoByUrl(String url){
		// GitHub URL에서 마지막 슬래시 뒤의 문자열을 추출하여 리턴
		String[] parts = url.split("com/");
		String repoName = parts[parts.length - 1];
		// ".git" 확장자가 있다면 제거
		if (repoName.endsWith(".git")) {
			repoName = repoName.substring(0, repoName.length() - 4);
		}
		return repoName;
	}
}
