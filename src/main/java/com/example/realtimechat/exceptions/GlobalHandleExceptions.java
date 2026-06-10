package com.example.realtimechat.exceptions;

import java.net.URI;
import java.sql.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalHandleExceptions {
	private final String VALIDATION_TITLE = "Validation Failed",
			ACCOUN_NOT_FOUND = "Tài khoản không tồn tại";

	private ProblemDetail createProblemDetail(HttpServletRequest httpRequest,
			String title, Map<?, ?> errors) {
		var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		detail.setTitle(title);
		detail.setProperty("errors", errors);
		detail.setInstance(URI.create(httpRequest.getRequestURI()));
		detail.setProperty("timestamp", new Date(System.currentTimeMillis()));
		return detail;
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleException(@NonNull MethodArgumentNotValidException e,
			@NonNull HttpServletRequest httpRequest) {
		var errors = e.getFieldErrors().stream().collect(Collectors.groupingBy(
				FieldError::getField,
				Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
		var detail = createProblemDetail(httpRequest, VALIDATION_TITLE, errors);
		return ResponseEntity.status(detail.getStatus()).body(detail);
	}

	@ExceptionHandler(value = AccountNotFoundException.class)
	public ResponseEntity<?> handleException(@NonNull AccountNotFoundException e,
			@NonNull HttpServletRequest httpRequest) {
		log.error(e.getMessage(), e);
		var detail = createProblemDetail(httpRequest, ACCOUN_NOT_FOUND,
				Map.of("message", "Tài khoản không tồn tại"));
		return ResponseEntity.status(detail.getStatus()).body(detail);
	}

}
