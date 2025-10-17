package com.api.projects.dtos.task;

import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskRequestDTO {
  @NotEmpty(message = "Title cannot be empty")
  private String title;

  @Size(max = 1000, message = "Description cannot exceed 1000 characters")
  private String description;

  @ApiModelProperty(
      value = "Status of the task",
      required = true,
      allowableValues = "TODO,DOING,DONE")
  private Status status;

  @ApiModelProperty(
      value = "Priority of the task",
      required = true,
      allowableValues = "LOW,MEDIUM,HIGH")
  private Priority priority;

  @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm]")
  @Future(message = "Duo date must be in the future")
  private LocalDateTime dueDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm]")
  @Future(message = "End date must be in the future")
  private LocalDateTime endDate;

  @NotNull(message = "Project ID cannot be null")
  private Long projectId;
}
