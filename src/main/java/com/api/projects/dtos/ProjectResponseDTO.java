package com.api.projects.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponseDTO {
  private Long id;
  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
