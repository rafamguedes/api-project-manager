package com.api.projects.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequestDTO {
  @NotEmpty(message = "Name cannot be empty")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @Pattern(
      regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$",
      message = "Start date must be in the format yyyy-MM-ddTHH:mm")
  private LocalDateTime startDate;

  @Pattern(
      regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$",
      message = "End date must be in the format yyyy-MM-ddTHH:mm")
  private LocalDateTime endDate;
}
