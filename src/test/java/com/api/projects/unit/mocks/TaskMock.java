package com.api.projects.unit.mocks;

import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.task.TaskFilterDTO;
import com.api.projects.dtos.task.TaskPriorityUpdateDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.dtos.task.TaskStatusUpdateDTO;
import com.api.projects.entities.Project;
import com.api.projects.entities.Task;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public class TaskMock {

  // Project Mocks
  public static Project createProject() {
    return Project.builder()
        .id(1L)
        .name("Project A")
        .description("Project Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(10))
        .build();
  }

  public static ProjectResponseDTO createProjectResponseDTO() {
    return ProjectResponseDTO.builder()
        .id(1L)
        .name("Project A")
        .description("Project Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(10))
        .build();
  }

  // TaskRequestDTO Mocks
  public static TaskRequestDTO createTaskRequestDTO() {
    return TaskRequestDTO.builder()
        .title("Task 1")
        .description("Task Description 1")
        .status(Status.TODO)
        .priority(Priority.LOW)
        .dueDate(LocalDateTime.now().plusDays(2))
        .projectId(1L)
        .build();
  }

  // Task Entity Mocks
  public static Task createTaskEntity() {
    return Task.builder()
        .title("Task 1")
        .description("Task Description 1")
        .status(Status.TODO)
        .priority(Priority.LOW)
        .dueDate(LocalDateTime.now().plusDays(2))
        .project(createProject())
        .build();
  }

  public static Task createSavedTaskEntity() {
    return Task.builder()
        .id(10L)
        .title("Task 1")
        .description("Task Description 1")
        .status(Status.TODO)
        .priority(Priority.LOW)
        .dueDate(LocalDateTime.now().plusDays(2))
        .project(createProject())
        .build();
  }

  // TaskResponseDTO Mocks
  public static TaskResponseDTO createTaskResponseDTO() {
    return TaskResponseDTO.builder()
        .id(10L)
        .title("Task 1")
        .description("Task Description 1")
        .status(Status.TODO)
        .priority(Priority.LOW)
        .dueDate(LocalDateTime.now().plusDays(2))
        .project(createProjectResponseDTO())
        .build();
  }

  // TaskStatusUpdateDTO Mocks
  public static TaskStatusUpdateDTO createTaskStatusUpdateDTO(Status status) {
    return TaskStatusUpdateDTO.builder().status(status).build();
  }

  // TaskPriorityUpdateDTO Mocks
  public static TaskPriorityUpdateDTO createTaskPriorityUpdateDTO(Priority priority) {
    return TaskPriorityUpdateDTO.builder().priority(priority).build();
  }

  // TaskFilterDTO Mocks
  public static TaskFilterDTO createTaskFilterDTO() {
    TaskFilterDTO filter = new TaskFilterDTO();
    filter.setPage(0);
    filter.setSize(10);
    filter.setSortBy("title");
    filter.setDirection("ASC");
    filter.setStatus(Status.TODO);
    filter.setPriority(Priority.LOW);
    filter.setProjectId(1L);
    return filter;
  }

  public static TaskFilterDTO createTaskFilterDTOWithNullFields() {
    TaskFilterDTO filter = new TaskFilterDTO();
    // Using default values: page=0, size=10, sortBy="id", direction="ASC"
    return filter;
  }

  // Page and Pagination Mocks
  public static Page<Task> createTaskPage(List<Task> tasks, Pageable pageable) {
    return new PageImpl<>(tasks, pageable, tasks.size());
  }

  public static Page<Task> createEmptyTaskPage(Pageable pageable) {
    return Page.empty(pageable);
  }

  public static Pageable createPageable(
      Integer page, Integer size, String sortBy, String direction) {
    return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
  }

  // Specific Scenario Mocks
  public static Task createTaskForUpdateStatus(Long id, Status currentStatus) {
    return Task.builder()
        .id(id)
        .title("Task to Update")
        .description("Task description")
        .status(currentStatus)
        .priority(Priority.MEDIUM)
        .dueDate(LocalDateTime.now().plusDays(5))
        .project(createProject())
        .build();
  }

  public static Task createTaskForUpdatePriority(Long id, Priority currentPriority) {
    return Task.builder()
        .id(id)
        .title("Task to Update")
        .description("Task description")
        .status(Status.TODO)
        .priority(currentPriority)
        .dueDate(LocalDateTime.now().plusDays(5))
        .project(createProject())
        .build();
  }
}
