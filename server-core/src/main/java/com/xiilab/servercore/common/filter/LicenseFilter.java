package com.xiilab.servercore.common.filter;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xiilab.modulecommon.exception.ErrorResponse;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.servercore.license.service.LicenseService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LicenseFilter extends OncePerRequestFilter {

	private final LicenseService licenseService;

	private final ObjectMapper objectMapper;


	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws RestApiException,
		ServletException,
		IOException {

		String uri = request.getRequestURI();
		String method = request.getMethod();
		if (method.equals("POST") && uri.equals("/api/v1/core/license")|| uri.contains("/actuator/health")) {
			log.debug("Exclusion : 라이센스 필터 제외");
			filterChain.doFilter(request, response);
		} else {
			log.debug("normality : 라이센스 필터 적용");
			try {
				licenseService.checkLicense();
				filterChain.doFilter(request, response);
			} catch (RestApiException e) {
				setErrorResponse(request, response, e.getErrorCode().getCode(), e.getErrorCode().getMessage());
			}
		}
	}

	/**
	 * Filter에서 licenseException처리를 위한 메소드
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param status   해당 LicenseException status
	 * @param message  해당 LicenseException message
	 */
	public void setErrorResponse(HttpServletRequest request, HttpServletResponse response, int status, String message) throws IOException {
		//String에 LocalDate를 매핑하도록 jackson 구성을 위해 모듈 활성화
		objectMapper.registerModule(new JavaTimeModule());
		//json에서 날짜를 문자열로 표시하도록 매퍼에게 지시
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		response.getWriter().write(objectMapper.writeValueAsString(
			ErrorResponse.builder()
				.resultCode(status)
				.resultMsg(message)
				.build()));
	}
}
