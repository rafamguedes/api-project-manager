package com.api.projects.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ProjectRequestDTO {
  @NotEmpty(message = "Name cannot be empty")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm]")
  @FutureOrPresent(message = "Start date must be in the present or future")
  private LocalDateTime startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd['T'HH:mm]")
  @Future(message = "End date must be in the future")
  private LocalDateTime endDate;
}
