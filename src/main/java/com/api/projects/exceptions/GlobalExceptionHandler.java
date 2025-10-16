package com.api.projects.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

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

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleDateValidationException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    String detail = "Request body is invalid or malformed";

    if (ex.getMessage() != null && ex.getMessage().contains("java.time.")) {
      detail = "Invalid date format. Use 'yyyy-MM-ddTHH:mm' (e.g., 2025-10-16T14:30)";
    }

    ProblemDetail problem =
        new ProblemDetail(
            "Malformed JSON request",
            HttpStatus.BAD_REQUEST.value(),
            detail,
            getRequestPath(request));

    problem.setProperty("expectedFormat", "yyyy-MM-ddTHH:mm (e.g., 2025-10-16T14:30)");
    problem.setProperty(
        "example",
        Map.of(
            "startDate", "2025-10-16T09:00",
            "endDate", "2025-10-16T18:00"));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

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

  private String getRequestPath(HttpServletRequest request) {
    return request.getRequestURI();
  }
}
