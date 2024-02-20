package com.xiilab.servercore.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.xiilab.modulecommon.exception.ErrorCode;
import com.xiilab.modulecommon.exception.ErrorResponse;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<Object> handleCustomException(RestApiException e) {
		log.error("handleCustomException", e);
		ErrorCode errorCode = e.getErrorCode();
		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
		log.error("handleIllegalArgument", e);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(errorCode, e.getMessage());
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleAllException(Exception ex) {
		log.error("handleAllException", ex);
		log.error(String.valueOf(ex.getStackTrace()));
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return handleExceptionInternal(errorCode, ex.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleControllerArgumentNotValid(ConstraintViolationException ex) {
		log.error("handleControllerArgumentNotValid", ex);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(ex, errorCode);
	}
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
		WebRequest request) {
		log.error("handleIllegalArgument", ex);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(ex, errorCode);
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode));
	}
	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode, message));
	}
	private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(e, errorCode));
	}

	private ResponseEntity<Object> handleExceptionInternal(ConstraintViolationException ex,
		ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(ex, errorCode));
	}
	private ErrorResponse makeErrorResponse(final ErrorCode errorCode) {
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(errorCode.getMessage())
			.build();
	}
	private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(message)
			.build();
	}
	private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
		List<ErrorResponse.ValidationError> errorList = e.getBindingResult().getFieldErrors()
			.stream()
			.map(ErrorResponse.ValidationError::of)
			.collect(Collectors.toList());
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(errorCode.getMessage())
			.errors(errorList)
			.build();
	}
	private ErrorResponse makeErrorResponse(ConstraintViolationException ex, ErrorCode errorCode) {
		List<ErrorResponse.ValidationError> errorList = new ArrayList<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			String field = violation.getPropertyPath().toString().split("\\.")[1];
			String message = violation.getMessage();
			errorList.add(ErrorResponse.ValidationError.of(field, message));
		}
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(errorCode.getMessage())
			.errors(errorList)
			.build();
	}

}
