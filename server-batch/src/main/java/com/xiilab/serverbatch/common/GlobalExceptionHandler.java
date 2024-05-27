package com.xiilab.serverbatch.common;

import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.xiilab.modulecommon.exception.ErrorCode;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({SchedulerException.class})
	public ResponseEntity<Object> handleSchedulerException(SchedulerException ex) {
		log.error("handleAllException", ex);
		log.error(String.valueOf(ex.getStackTrace()));
		return ResponseEntity.internalServerError().body(ex.getMessage());
	}

	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<Object> hadleIllegalArgsException(IllegalArgumentException ex) {
		log.error("handleAllException", ex);
		log.error(String.valueOf(ex.getStackTrace()));
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
