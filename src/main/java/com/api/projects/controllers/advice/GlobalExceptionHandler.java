package com.api.projects.controllers.advice;

import com.api.projects.services.exceptions.BusinessException;
import com.api.projects.services.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
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

  /**
   * Handle authentication failures due to bad credentials.
   *
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
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

  /**
   * Handle validation errors for @Valid annotated request bodies.
   *
   * @param ex the MethodArgumentNotValidException
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with validation error details
   */
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

  /**
   * Handle type mismatch errors for request parameters.
   *
   * @param ex the MethodArgumentTypeMismatchException
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
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

  /**
   * Handle malformed JSON requests, including invalid date formats.
   *
   * @param ex the HttpMessageNotReadableException
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
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

  /**
   * Handle entity not found exceptions.
   *
   * @param ex the NotFoundException
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
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

  /**
   * Handle custom business exceptions.
   *
   * @param ex the BusinessException
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
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

  /**
   * Handle all other uncaught exceptions.
   *
   * @param ex the Exception
   * @param request the HttpServletRequest
   * @return a ResponseEntity containing a ProblemDetail with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(
      Exception ex, HttpServletRequest request) {

    log.error("Internal server error: ", ex);

    ProblemDetail problem =
        new ProblemDetail(
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            getRequestPath(request));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  /**
   * Extract the request path from the HttpServletRequest.
   *
   * @param request the HttpServletRequest
   * @return the request URI
   */
  private String getRequestPath(HttpServletRequest request) {
    return request.getRequestURI();
  }
}
