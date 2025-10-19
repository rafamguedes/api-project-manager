package com.api.projects.controllers.advice;

import com.api.projects.exceptions.BusinessException;
import com.api.projects.exceptions.NotFoundException;
import com.api.projects.exceptions.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Authentication error",
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ProblemDetail> handleBadCredentialsException(HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Authentication failed",
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid username or password",
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    Map<String, String> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    fieldError ->
                        Optional.ofNullable(fieldError.getDefaultMessage())
                            .orElse("Invalid value")));

    ProblemDetail problem =
        new ProblemDetail(
            "Validation error",
            HttpStatus.BAD_REQUEST.value(),
            "One or more fields are invalid",
            getRequestPath(request));

    problem.setProperty("validationErrors", validationErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    String detail =
        String.format(
            "Parameter '%s' has invalid value '%s'. Expected type: %s",
            ex.getName(),
            ex.getValue(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

    ProblemDetail problem =
        new ProblemDetail(
            "Invalid parameter", HttpStatus.BAD_REQUEST.value(), detail, getRequestPath(request));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    String detail = "Request body is invalid or malformed";

    if (ex.getCause() != null) {
      detail += ": " + ex.getCause().getMessage();
    }

    ProblemDetail problem =
        new ProblemDetail(
            "Malformed JSON request",
            HttpStatus.BAD_REQUEST.value(),
            detail,
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ProblemDetail> handleEntityNotFound(
      NotFoundException ex, HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Resource not found",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Business rule violation",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<ProblemDetail> handleRateLimitExceeded(
      RateLimitExceededException ex, HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Rate limit exceeded",
            HttpStatus.TOO_MANY_REQUESTS.value(),
            ex.getMessage(),
            getRequestPath(request));

    problem.setProperty("retryAfterSeconds", ex.getRetryAfterSeconds());

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
        .body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(HttpServletRequest request) {

    ProblemDetail problem =
        new ProblemDetail(
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  private String getRequestPath(HttpServletRequest request) {
    return request.getRequestURI();
  }
}
