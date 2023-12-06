package com.xiilab.servercore.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommonMessageUtils {
	private final MessageSource messageSource;

	/**
	 * 언어팩 언어 가져오기
	 *
	 * @param code 언어팩 코드
	 * @return String
	 */

	public String getMessage(String code) {
		System.out.println(LocaleContextHolder.getLocale());
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}

	/**
	 * 언어팩 언어 가져오기
	 *
	 * @param code 언어팩 코드
	 * @param strs 동적 문자
	 * @return String
	 */

	public String getMessage(String code, String[] strs) {
		return messageSource.getMessage(code, strs, LocaleContextHolder.getLocale());
	}
}
