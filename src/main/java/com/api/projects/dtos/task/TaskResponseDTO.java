package com.api.projects.dtos.task;

import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDTO {
  private Long id;
  private String title;
  private String description;
  private Status status;
  private LocalDateTime dueDate;
  private Priority priority;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String updatedBy;
  private ProjectResponseDTO project;
}
