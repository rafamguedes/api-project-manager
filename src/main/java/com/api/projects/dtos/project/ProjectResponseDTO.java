package com.api.projects.dtos.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponseDTO {
  private Long id;
  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
