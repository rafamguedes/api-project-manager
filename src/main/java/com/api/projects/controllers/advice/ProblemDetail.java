package com.api.projects.controllers.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetail {
  private String title;
  private int status;
  private String detail;
  private String instance;
  private LocalDateTime timestamp;
  private Map<String, Object> properties;

  public ProblemDetail(String title, int status, String detail, String instance) {
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
    this.timestamp = LocalDateTime.now();
    this.properties = new HashMap<>();
  }

  public void setProperty(String key, Object value) {
    this.properties.put(key, value);
  }
}
