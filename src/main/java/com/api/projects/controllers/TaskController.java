package com.api.projects.controllers;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.task.TaskFilterDTO;
import com.api.projects.dtos.task.TaskPriorityUpdateDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.dtos.task.TaskStatusUpdateDTO;
import com.api.projects.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Create Task", description = "Create a new task")
  public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request) {
    TaskResponseDTO response = taskService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Get Task by ID", description = "Retrieve a task by its ID")
  public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
    TaskResponseDTO response = taskService.findById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(
      summary = "Get Tasks with Filtering",
      description = "Retrieve a paginated list of tasks with optional filtering")
  public ResponseEntity<PageResponseDTO<TaskResponseDTO>> getTasks(@Valid TaskFilterDTO filter) {
    PageResponseDTO<TaskResponseDTO> response = taskService.findByFilter(filter);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/status")
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Update Task Status", description = "Update the status of a task by its ID")
  public ResponseEntity<Void> updateTaskStatus(
      @PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO request) {
    taskService.updateStatus(id, request);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/priority")
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(
      summary = "Update Task Priority",
      description = "Update the priority of a task by its ID")
  public ResponseEntity<Void> updateTaskPriority(
      @PathVariable Long id, @Valid @RequestBody TaskPriorityUpdateDTO request) {
    taskService.updatePriority(id, request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Delete Task", description = "Delete a task by its ID")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
