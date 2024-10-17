package com.xiilab.modulecommon.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RepositoryUrlUtils {

	public static String convertRepoUrlToRepoName(CodeType codeType, String url) {
		if (Objects.isNull(url)) {
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}
		if (url.contains("/scm/") && codeType == CodeType.BIT_BUCKET) {
			return getRepoName(url.split("/scm/"));
		}
		if (url.contains(".com") && codeType != CodeType.BIT_BUCKET) {
			return getRepoName(url.split("com/"));
		}
		if (url.contains(".org")) {
			return getRepoName(url.split("org/"));
		}

		log.error("convertRepoUrlToRepoName() URL format error. url = {}", url);
		throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
	}


	// URL에서 도메인을 추출하는 메서드
	public static String extractDomain(String url) {
		// 정규식을 통해 https:// 뒤에 오는 도메인만 추출
		String regex = "(https?://[^/]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(1);
		} else {
			log.error("extractDomain() URL format error. url = {}", url);
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}
	}

	// URL에서 레포지토리 이름을 추출하는 메서드
	public static String getRepoName(String[] parts) {
		// URL에서 마지막 슬래시 뒤의 문자열을 추출하여 리턴
		String repoName = parts[parts.length - 1];
		// ".git" 확장자가 있다면 제거
		if (repoName.endsWith(".git")) {
			repoName = repoName.substring(0, repoName.length() - 4);
		}
		return repoName;
	}
}
