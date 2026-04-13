package com.example.realtimechat.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.security.InvalidParameterException;
import java.sql.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandleExceptions {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(@NonNull MethodArgumentNotValidException e, @NonNull HttpServletRequest httpRequest) {
        var errors = e.getFieldErrors().stream().collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
        ));
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation Failed");
        detail.setProperty("errors", errors);
        detail.setInstance(URI.create(httpRequest.getRequestURI()));
        detail.setProperty("timestamp", new Date(System.currentTimeMillis()));
        return ResponseEntity.status(detail.getStatus()).body(detail);
    }
}
