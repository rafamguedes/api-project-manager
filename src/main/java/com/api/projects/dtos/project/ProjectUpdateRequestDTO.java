package com.api.projects.dtos.project;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class ProjectUpdateRequestDTO {

  @Size(max = 100, message = "Name cannot exceed 100 characters")
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
