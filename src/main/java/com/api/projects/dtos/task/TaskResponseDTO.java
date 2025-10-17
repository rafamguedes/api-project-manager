package com.api.projects.dtos.task;

import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDTO {
  private Long id;
  private String title;
  private String description;
  private Status status;
  private Priority priority;
  private ProjectResponseDTO project;
}
