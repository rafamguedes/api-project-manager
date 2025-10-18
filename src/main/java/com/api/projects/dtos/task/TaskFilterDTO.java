package com.api.projects.dtos.task;

import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDTO {
  private Integer page = 0;
  private Integer size = 10;
  private String sortBy = "id";
  private String direction = "ASC";
  private Status status;
  private Priority priority;
  private Long projectId;
}
