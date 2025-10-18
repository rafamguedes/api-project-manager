package com.api.projects.integration.mocks;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public class TaskMock {

  public static ProjectResponseDTO createProjectResponseDTO() {
    return ProjectResponseDTO.builder()
        .id(1L)
        .name("Integration Test Project")
        .description("Project for integration testing")
        .startDate(LocalDateTime.parse("2390-10-17T18:00:00"))
        .endDate(LocalDateTime.parse("2390-11-16T18:00:00"))
        .build();
  }

  public static TaskRequestDTO createTaskRequestDTO() {
    return TaskRequestDTO.builder()
        .title("Integration Task")
        .description("Task for integration testing")
        .status(Status.TODO)
        .priority(Priority.MEDIUM)
        .dueDate(LocalDateTime.parse("2390-10-20T09:00:00"))
        .projectId(1L)
        .build();
  }

  public static TaskResponseDTO createTaskResponseDTO() {
    return TaskResponseDTO.builder()
        .id(10L)
        .title("Integration Task")
        .description("Task for integration testing")
        .status(Status.TODO)
        .priority(Priority.MEDIUM)
        .dueDate(LocalDateTime.parse("2390-10-20T09:00:00"))
        .project(createProjectResponseDTO())
        .build();
  }

  public static PageResponseDTO<TaskResponseDTO> createTaskPageResponseDTO() {
    return new PageResponseDTO<>(List.of(createTaskResponseDTO()), 0, 1, 1, 10, true, true);
  }
}
