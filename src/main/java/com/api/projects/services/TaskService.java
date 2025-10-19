package com.api.projects.services;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.task.TaskFilterDTO;
import com.api.projects.dtos.task.TaskPriorityUpdateDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.dtos.task.TaskStatusUpdateDTO;
import com.api.projects.entities.Project;
import com.api.projects.entities.Task;
import com.api.projects.mappers.TaskMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.repositories.TaskRepository;
import com.api.projects.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
  private static final String PROJECT_NOT_FOUND_MESSAGE = "Project not found by id: ";
  private static final String TASK_NOT_FOUND_MESSAGE = "Task not found by id: ";

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final TaskMapper taskMapper;

  public TaskResponseDTO create(TaskRequestDTO request) {
    Project project =
        projectRepository
            .findById(request.getProjectId())
            .orElseThrow(
                () -> new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + request.getProjectId()));

    Task task = taskMapper.toEntity(request);
    task.setProject(project);

    Task savedTask = taskRepository.save(task);

    return taskMapper.toResponse(savedTask);
  }

  public TaskResponseDTO findById(Long id) {
    return taskRepository
        .findById(id)
        .map(taskMapper::toResponse)
        .orElseThrow(() -> new NotFoundException(TASK_NOT_FOUND_MESSAGE + id));
  }

  public PageResponseDTO<TaskResponseDTO> findByFilter(TaskFilterDTO filter) {
    Pageable pageable =
        PageRequest.of(
            filter.getPage(),
            filter.getSize(),
            Sort.by(Sort.Direction.fromString(filter.getDirection()), filter.getSortBy()));

    Page<TaskResponseDTO> pageResult =
        taskRepository
            .findByFilters(
                filter.getStatus(), filter.getPriority(), filter.getProjectId(), pageable)
            .map(taskMapper::toResponse);

    return PageResponseDTO.of(pageResult);
  }

  public void updateStatus(Long id, TaskStatusUpdateDTO request) {
    Task existingTask =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(TASK_NOT_FOUND_MESSAGE + id));

    existingTask.setStatus(request.getStatus());
    taskRepository.save(existingTask);
  }

  public void updatePriority(Long id, TaskPriorityUpdateDTO request) {
    Task existingTask =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(TASK_NOT_FOUND_MESSAGE + id));

    existingTask.setPriority(request.getPriority());
    taskRepository.save(existingTask);
  }

  public void delete(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new NotFoundException(TASK_NOT_FOUND_MESSAGE + id);
    }

    taskRepository.deleteById(id);
  }
}
