package com.xiilab.modulecommon.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.dto.WeekRangeDTO;
import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.RestApiException;
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
	 *
	 * @param unixTime Prometheus에서 조회된 UnixTime
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
	 * @param node      JsonNode
	 * @param fieldName 필드 이름
	 * @return 가져온 필드 값 또는 Null
	 */
	public static String getStringOrNullByJsonNode(JsonNode node, String fieldName) {
		JsonNode field = node.get(fieldName);
		return field == null ? "" : field.asText();
	}
	public static String getInstance(String str) {
		try {
			JsonNode root = objectMapper.readTree(str);
			return root.path("data").path("result").elements().next().get("metric").get("instance").asText();
		} catch (NoSuchElementException e){
			return "";
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonErrorCode.DATA_FORMAT_FAIL);
		}
	}

	/**
	 * DateTime UnixTime으로 변환하는 메소드
	 *
	 * @param formattedDateTime 변환될 Date Time
	 * @return 변경된 UnixTime
	 */
	public static String toUnixTime(String formattedDateTime) {
		LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, DateTimeFormatter.ofPattern(dateFormat));
		return String.valueOf(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
	}

	/**
	 * data 소수점 두번째 자리 반올림 메소드
	 *
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
	 *
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
	 *
	 * @param metric 매핑될 metric
	 * @return 매핑된 값
	 */
	public static Iterator<JsonNode> formatJsonNode(String metric) {
		try {
			return objectMapper.readTree(metric).get("data").get("result").elements();
		} catch (JsonProcessingException e) {
			throw new CommonException(CommonErrorCode.DATA_FORMAT_FAIL);
		}
	}

	public static String convertGitHubRepoUrlToRepoName(String url) {
		// GitHub URL에서 마지막 슬래시 뒤의 문자열을 추출하여 리턴
		String[] parts = url.split("com/");
		String repoName = parts[parts.length - 1];
		// ".git" 확장자가 있다면 제거
		if (repoName.endsWith(".git")) {
			repoName = repoName.substring(0, repoName.length() - 4);
		}
		return repoName;
	}

	public static String convertGitlabRepoUrlToProjectName(String url) {
		// "http://192.168.1.151/x-trainer/xlabeller-deployment.git" -> "xlabeller-deployment"
		return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
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
	 *
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
	 *
	 * @return 합산된 문자
	 */
	public static int parseAndSum(String x, String y) {
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

	public static long fewMinutesAgo(Instant now, String lastTimestamp) {
		// 몇분전 발생인지 계산
		Instant eventTime = Instant.parse(lastTimestamp)
			.truncatedTo(ChronoUnit.MINUTES);
		return ChronoUnit.MINUTES.between(eventTime, now);
	}

	public static String getCurrentUnixTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return DataConverterUtil.toUnixTime(LocalDateTime.now().format(formatter));
	}

	public static String subtractMinutesFromCurrentTime(long minutes) {
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
			long setp = (timeMil2 - timeMil1);

			return setp;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getFortyStep(String startDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			// Date -> 밀리세컨즈
			long timeMil1 = start.getTime();
			long timeMil2 = end.getTime();
			long step = timeMil2 - timeMil1;
			if(step >= 200000){
				step = step / 200000;
			}else if(step >= 60000) {
				step = step / 60000;
			}else{
				step = 1L;
			}
			return step;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getStepByUnixTime(String start, String end){
		return (Long.parseLong(end) - Long.parseLong(start)) / 31;
	}

	public static long getSystemStep(String startDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			// Date -> 밀리세컨즈
			long timeMil1 = start.getTime();
			long timeMil2 = end.getTime();
			long setp = (timeMil2 - timeMil1) / 100;

			return setp;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 알림 발생시간 생성하는 메소드
	 *
	 * @return 발생된 시간 "2월 24일 금요일 오후 4:21"
	 */
	public static String getCurrentTime(LocalDateTime realTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return realTime.format(formatter);
	}

	public static LocalDateTime dataFormatterByStr(String date) {
		if (!StringUtils.isEmpty(date)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			return LocalDateTime.parse(date, formatter);
		} else {
			return null;
		}
	}

	public static LocalDateTime dataFormatterBy16Str(String date) {
		if (!StringUtils.isEmpty(date)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
			return LocalDateTime.parse(date, formatter);
		} else {
			return null;
		}
	}

	public static String getEndDateUnixTime(String startDate, String reportType) {
		LocalDateTime dateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(dateFormat));
		LocalDateTime endDate;
		ReportType type = ReportType.valueOf(reportType);
		if (type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)) {
			endDate = dateTime.minusWeeks(1);
		} else {
			if (ReportType.MONTHLY_CLUSTER.equals(type) || ReportType.MONTHLY_SYSTEM.equals(type)) {
				endDate = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0, 0);
			} else {
				endDate = dateTime.minusMonths(1);
			}
		}
		return String.valueOf(endDate.atZone(ZoneId.systemDefault()).toEpochSecond());
	}

	public static String getEndDate(String startDate, String reportType) {
		LocalDateTime dateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(dateFormat));
		LocalDateTime endDate;
		ReportType type = ReportType.valueOf(reportType);
		if (type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)) {
			endDate = dateTime.minusDays(6);
		} else {
			if (ReportType.MONTHLY_CLUSTER.equals(type) || ReportType.MONTHLY_SYSTEM.equals(type)) {
				endDate = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0, 0);
			} else {
				endDate = dateTime.minusMonths(1);
			}
		}
		return endDate.toString().replace("T", " ") + ":00";
	}

	public static String plusDay(String date, long plusAmount) {
		LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateFormat));
		dateTime = dateTime.plusDays(plusAmount);
		return dateTime.format(DateTimeFormatter.ofPattern(dateFormat));
	}

	public static int getWeeksInMonth(String date) {
		LocalDate dateTime = LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));
		YearMonth yearMonth = YearMonth.from(dateTime);

		// 월의 첫날과 마지막날을 구함
		LocalDate firstDayOfMonth = yearMonth.atDay(1);
		LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

		// 월의 첫째 주와 마지막 주를 구함
		int firstWeek = firstDayOfMonth.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		int lastWeek = lastDayOfMonth.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);

		// 첫째 주의 일요일을 구함
		LocalDate firstSunday = firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
		if (firstSunday.isBefore(firstDayOfMonth)) {
			// 첫 주가 전월에 속하는 경우
			firstWeek++;
		}

		// 주 수 계산
		int weeksInMonth = lastWeek - firstWeek + 1;
		return weeksInMonth;
	}

	public static long getReportStep(String reportType) {
		ReportType type = ReportType.valueOf(reportType);
		if (type.equals(ReportType.WEEKLY_CLUSTER) || type.equals(ReportType.WEEKLY_SYSTEM)) {
			return 4000L;
		} else {
			return 16000L;
		}
	}

	public static String dateFormatMMDD(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date date = sdf.parse(dateString);
			SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM-dd");
			return monthDayFormat.format(date);
		} catch (ParseException e) {
			throw new RuntimeException("날짜 형식이 올바르지 않습니다.", e);
		}
	}

	public static String dateFormat(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return dateTime.format(formatter);
	}

	public static WeekRangeDTO getWeekDateRange(String date, int weekSeq) {
		LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateFormat));
		localDate = localDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDateTime startDate = localDate.plusWeeks(weekSeq - 1);
		if (localDate.getMonth() != startDate.getMonth()) {
			throw new RestApiException(CommonErrorCode.MONTH_WEEKS_OUT_OF_RANGE);
		}
		LocalDateTime mondayDate = getMondayDate(startDate);
		LocalDateTime sundayDate = getSundayDate(startDate);

		// 입력된 날짜의 월과 주의 시작 및 끝 날짜의 월이 다를 경우 조정
		if (mondayDate.getMonth() != startDate.getMonth()) {
			mondayDate = startDate.with(TemporalAdjusters.firstDayOfMonth());
		}
		if (sundayDate.getMonth() != startDate.getMonth()) {
			sundayDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
		}

		return new WeekRangeDTO(mondayDate.format(DateTimeFormatter.ofPattern(dateFormat)),
			sundayDate.format(DateTimeFormatter.ofPattern(dateFormat)));
	}

	public static String getDateMonthWeek(String date) {
		LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateFormat));
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		int month = localDate.getMonth().getValue();
		int week = localDate.get(weekFields.weekOfMonth());
		return String.format("%02d-%02d", month, week);
	}

	public static String convertToMonthDay(String dateTimeStr) {
		// 입력된 문자열을 LocalDateTime 객체로 파싱
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// LocalDateTime 객체를 원하는 포맷("MM-dd")으로 변환
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
		return dateTime.format(formatter);
	}

	public static LocalDateTime getMondayDate(LocalDateTime date) {
		LocalDateTime localDateTime = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return LocalDateTime.parse(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
	}

	public static LocalDateTime getSundayDate(LocalDateTime date) {
		LocalDateTime localDateTime = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		return LocalDateTime.parse(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
	}

	public static String convertUTCDateToKorDate(String utcTime) {
		try {
			LocalDateTime startTimeUTC = LocalDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME)
				.plusHours(9L);
			ZonedDateTime startTimeKorea = startTimeUTC.atZone(ZoneId.of("Asia/Seoul"));
			// 변환된 시간을 원하는 형식으로 포맷
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return startTimeKorea.format(formatter);
		} catch (DateTimeParseException e) {
			throw new RestApiException(CommonErrorCode.FAILED_UTC_TO_KOR_TIME);
		}
	}

	// MB -> GB 변환 메서드
	public static double convertMbToGb(int mb) {
		double gb = mb / 1024.0;
		BigDecimal bd = new BigDecimal(gb).setScale(1, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	// LDT to "yyyy-MM-dd HH:mm:ss" 포맷 출력
	public static String convertLocalDateTimeToString(LocalDateTime date) {
		try {
			return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			return null;
		}
	}
}
