package com.xiilab.servercore.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.xiilab.modulecommon.exception.ErrorCode;
import com.xiilab.modulecommon.exception.ErrorResponse;
import com.xiilab.modulecommon.exception.K8sException;
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
		ErrorCode errorCode = e.getErrorCode();
		String target = e.getTarget();
		if (!target.isBlank()) {
			String msg = String.format(errorCode.getMessage(), target);
			log.error("restApiException :" + msg);
			return customExceptionInternal(errorCode, msg);
		}
		log.error("restApiException :" + errorCode.getMessage());
		return customExceptionInternal(errorCode);
	}

	@ExceptionHandler({WebClientResponseException.class})
	public ResponseEntity<Object> handleWebClientException(WebClientResponseException e) {
		//HTTP 오류 처리
		int rawStatusCode = e.getRawStatusCode();
		String errMsg = e.getResponseBodyAsString();
		log.error(errMsg);
		ErrorCode errorCode = switch (rawStatusCode) {
			case 400 -> CommonErrorCode.INVALID_PARAMETER;
			case 401 -> CommonErrorCode.UNAUTHORIZED_ERROR;
			case 409 -> CommonErrorCode.CONFLICT_ERROR;
			case 500 -> CommonErrorCode.INTERNAL_SERVER_ERROR;
			default -> null;
		};
		return customExceptionInternal(errorCode);
	}

	@ExceptionHandler(K8sException.class)
	public ResponseEntity<Object> handleK8sException(K8sException e) {
		ErrorCode errorCode = e.getErrorCode();
		String msg = String.format(errorCode.getMessage());
		log.error("k8sException :" + msg);
		return handleExceptionInternal(errorCode, msg);
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
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {
		log.error("handleIllegalArgument", ex);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		// String errorMessage = String.format(INVALID_DTO_FIELD_ERROR_MESSAGE_FORMAT, firstFieldError.getField(),
		// 	firstFieldError.getDefaultMessage(), firstFieldError.getRejectedValue());
		return handleExceptionInternal(ex, errorCode);
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		// String errorMessage = String.format(INVALID_DTO_FIELD_ERROR_MESSAGE_FORMAT, firstFieldError.getField(),
		// 	firstFieldError.getDefaultMessage(), firstFieldError.getRejectedValue());
		return handleExceptionInternal(ex, errorCode);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
		MissingServletRequestParameterException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {
		log.error("handleMissingServletRequestParameter", ex);
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		// String errorMessage = String.format(INVALID_DTO_FIELD_ERROR_MESSAGE_FORMAT, firstFieldError.getField(),
		// 	firstFieldError.getDefaultMessage(), firstFieldError.getRejectedValue());
		return handleExceptionInternal(ex, errorCode);
	}

	private ResponseEntity<Object> customExceptionInternal(ErrorCode errorCode) {
		// return ResponseEntity.status(HttpStatus.OK)
		// 	.body(makeErrorResponse(errorCode));
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode));
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode));
	}

	private ResponseEntity<Object> customExceptionInternal(ErrorCode errorCode, String message) {
		// return ResponseEntity.status(HttpStatus.OK)
		// 	.body(makeErrorResponse(errorCode, message));
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode, message));
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(errorCode, message));
	}

	private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(e, errorCode));
	}

	private ResponseEntity<Object> handleExceptionInternal(MissingServletRequestParameterException ex,
		ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(ex, errorCode));
	}

	private ResponseEntity<Object> handleExceptionInternal(ConstraintViolationException ex,
		ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getCode())
			.body(makeErrorResponse(ex, errorCode));
	}

	private ResponseEntity<Object> handleExceptionInternal(HandlerMethodValidationException ex,
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

	private ErrorResponse makeErrorResponse(MissingServletRequestParameterException ex, ErrorCode errorCode) {
		// List<ErrorResponse.ValidationError> errorList = new ArrayList<>();
		// for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
		// 	String field = violation.getPropertyPath().toString().split("\\.")[1];
		// 	String message = violation.getMessage();
		// 	errorList.add(ErrorResponse.ValidationError.of(field, message));
		// }
		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(ex.getParameterName() + "가 누락되었습니다.")
			// .errors(errorList)
			.build();
	}

	private ErrorResponse makeErrorResponse(HandlerMethodValidationException ex, ErrorCode errorCode) {
		Optional<MessageSourceResolvable> messageSourceResolvable = ex.getAllValidationResults().stream()
			.findFirst() // 첫 번째 유효성 검사 결과 가져오기
			.flatMap(
				validationResult -> validationResult.getResolvableErrors().stream().findFirst());// 첫 번째 해결 가능한 오류 가져오기

		String defaultMessage = messageSourceResolvable
			.map(MessageSourceResolvable::getDefaultMessage) // 기본 메시지 가져오기
			.orElse("요청이 올바르지 않습니다."); // 기본 메시지 설정
		String parameter = messageSourceResolvable
			.map(MessageSourceResolvable::getArguments)
			.map(Arrays::stream)
			.orElseGet(Stream::empty)
			.map(arg -> (DefaultMessageSourceResolvable) arg)
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.findFirst()
			.orElse("UNKNOWN");

		log.error("Parameter is null : {}", parameter);

		return ErrorResponse.builder()
			.resultCode(errorCode.getCode())
			.resultMsg(defaultMessage)
			.build();
	}
}
