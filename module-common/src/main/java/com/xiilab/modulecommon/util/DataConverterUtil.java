package com.xiilab.modulecommon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

@Service
public class DataConverterUtil {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static long KILO_BYTE = 1024;
	private static long MEGA_BYTE = KILO_BYTE * 1024;
	private static long GIGA_BYTE = MEGA_BYTE * 1024;
	private static long TERA_BYTE = GIGA_BYTE * 1024;
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
	public static String formatObjectMapper(String metric) {
		try {
			JsonNode root = objectMapper.readTree(metric);
			JsonNode valueNode = root.path("data").path("result");

			if (valueNode.isMissingNode() || !valueNode.elements().hasNext()) {
				return "0";
			}

			return valueNode.elements().next().get("value").get(1).asText();
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

	// 메모리 요청의 단위를 킬로바이트(KiB)로 변환하는 메서드
	public static long convertToKiB(String amount, String format) {
		return switch (format) {
			case "Gi" -> Long.parseLong(amount) * 1024 * 1024; // GiB -> KiB
			case "G" -> Long.parseLong(amount) * 1024 * 1024; // GB -> KiB
			case "Mi" -> Long.parseLong(amount) * 1024; // MiB -> KiB
			case "M" -> Long.parseLong(amount) * 1024; // MB -> KiB
			default -> Long.parseLong(amount); // 기본적으로 KiB로 가정
		};
	}
	// 메모리의 단위를 변환하는 메서드
	public static double convertToGBMemorySize(String amount) {
		double doubleValue = Double.parseDouble(amount); // 부동 소수점으로 파싱
		return doubleValue / GIGA_BYTE;
	}

	/**
	 * DISK 사이즈 계산하는 메소드
	 * @param bytes 계산될 Bytes
	 */
	public static double formatGBDiskSize(String bytes) {
		double doubleValue = Double.parseDouble(bytes); // 부동 소수점으로 파싱
		return doubleValue / GIGA_BYTE;
	}

	public static double convertToCPU(String resource) {
		return Double.parseDouble(resource); // 부동 소수점으로 파싱
	}
	public static double convertToGPU(String resource) {
		return Double.parseDouble(resource); // 부동 소수점으로 파싱
	}

	/**
	 * String 값 소수점 첫자리 반올림하여 계산해주는 메소드
	 */
	public static String roundToString(String request) {
		// m 값을 Core로 변환
		double result = Double.parseDouble(request);
		return String.format("%.2f", result);
	}
	public static double roundToNearestHalf(double number) {
		double integerPart = Math.floor(number);
		double decimalPart = number - integerPart;
		if (decimalPart < 0.25) {
			return integerPart;
		} else if (decimalPart < 0.75) {
			return integerPart + 0.5;
		} else {
			return integerPart + 1;
		}
	}
	/**
	 * 두 문자열을 가져왓 합산하는 메소드
	 * @return 합산된 문자
	 */
	public static int parseAndSum(String x, String y){
		return Integer.parseInt(x.replaceAll("[^0-9]", "")) + Integer.parseInt(y.replaceAll("[^0-9]", ""));
	}

	public static String datetimeFormatter(long fewMinutesAgo) {
		// 현재 시간
		LocalDateTime now = LocalDateTime.now();

		// 주어진 시간(fewMinutesAgo)만큼 현재 시간에서 빼기
		LocalDateTime fewMinutesAgoTime = now.minusMinutes(fewMinutesAgo);

		// AM/PM 형식으로 출력하기 위한 포매터 설정
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

		// 포맷 적용하여 문자열로 변환
		return fewMinutesAgoTime.format(formatter);
	}
	public static long fewMinutesAgo(Instant now, String lastTimestamp){
		// 몇분전 발생인지 계산
		Instant eventTime = Instant.parse(lastTimestamp)
			.truncatedTo(ChronoUnit.MINUTES);
		return ChronoUnit.MINUTES.between(eventTime, now);
	}
	public static String getCurrentUnixTime(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return DataConverterUtil.toUnixTime(LocalDateTime.now().format(formatter));
	}
	public static String subtractMinutesFromCurrentTime(long minutes){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return DataConverterUtil.toUnixTime(LocalDateTime.now().minusMinutes(minutes).format(formatter));
	}
	public static long getStep(String startDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			// Date -> 밀리세컨즈
			long timeMil1 = start.getTime();
			long timeMil2 = end.getTime();
			long setp = (timeMil2 - timeMil1)  / 40000;

			return setp;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	public static long getSystemStep(String startDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			// Date -> 밀리세컨즈
			long timeMil1 = start.getTime();
			long timeMil2 = end.getTime();
			long setp = (timeMil2 - timeMil1)  / 100;

			return setp;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 알림 발생시간 생성하는 메소드
	 * @return 발생된 시간 "2월 24일 금요일 오후 4:21"
	 */
	public static String getCurrentTime(LocalDateTime realTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return realTime.format(formatter);
	}

	public static LocalDateTime dataFormatterByStr(String date){
		if(!StringUtils.isEmpty(date)){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			return LocalDateTime.parse(date, formatter);
		}else {
			return null;
		}
	}
	public static LocalDateTime dataFormatterBy16Str(String date){
		if(!StringUtils.isEmpty(date)){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
			return LocalDateTime.parse(date, formatter);
		}else {
			return null;
		}
	}

	public static String getEndDateUnixTime(String startDate, String reportType){
		LocalDateTime dateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(dateFormat));
		LocalDateTime endDate;
		ReportType type = ReportType.valueOf(reportType);
		if(type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)){
			endDate = dateTime.minusWeeks(1);
		}else{
			endDate= dateTime.minusMonths(1);
		}
		return String.valueOf(endDate.atZone(ZoneId.systemDefault()).toEpochSecond());
	}

	public static String getEndDate(String startDate, String reportType){
		LocalDateTime dateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(dateFormat));
		LocalDateTime endDate;
		ReportType type = ReportType.valueOf(reportType);
		if(type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)){
			endDate = dateTime.minusDays(6);
		}else{
			endDate= dateTime.minusMonths(1);
		}

		return endDate.toString().replace("T", " ")+":00";
	}

	public static long getReportStep(String reportType){
		ReportType type = ReportType.valueOf(reportType);
		if(type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)){
			return 4000L;
		}else{
			return 16000L;
		}
	}

	public static String dateFormatMMDD(String dateString){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date date = sdf.parse(dateString);
			SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM-dd");
			return monthDayFormat.format(date);
		} catch (ParseException e) {
			throw new RuntimeException("날짜 형식이 올바르지 않습니다.", e);
		}
	}

	public static String dateFormat(LocalDateTime dateTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return dateTime.format(formatter);
	}
}
