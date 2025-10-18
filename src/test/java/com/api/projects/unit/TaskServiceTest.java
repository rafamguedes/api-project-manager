package com.api.projects.unit;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.task.TaskFilterDTO;
import com.api.projects.dtos.task.TaskPriorityUpdateDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.dtos.task.TaskStatusUpdateDTO;
import com.api.projects.entities.Project;
import com.api.projects.entities.Task;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import com.api.projects.mappers.TaskMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.repositories.TaskRepository;
import com.api.projects.services.TaskService;
import com.api.projects.services.exceptions.NotFoundException;
import com.api.projects.unit.mocks.TaskMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock private TaskRepository taskRepository;

  @Mock private ProjectRepository projectRepository;

  @Mock private TaskMapper taskMapper;

  @InjectMocks private TaskService taskService;

  @Test
  @DisplayName("Should create task successfully")
  void create_ShouldCreateTask_WhenValidRequest() {
    // Arrange
    TaskRequestDTO request = TaskMock.createTaskRequestDTO();
    Project project = TaskMock.createProject();
    Task task = TaskMock.createTaskEntity();
    Task savedTask = TaskMock.createSavedTaskEntity();
    TaskResponseDTO response = TaskMock.createTaskResponseDTO();

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(taskMapper.toEntity(request)).thenReturn(task);
    when(taskRepository.save(task)).thenReturn(savedTask);
    when(taskMapper.toResponse(savedTask)).thenReturn(response);

    // Act
    TaskResponseDTO result = taskService.create(request);

    // Assert
    assertNotNull(result);
    assertEquals(10L, result.getId());
    assertEquals("Task 1", result.getTitle());
    assertEquals(Status.TODO, result.getStatus());

    verify(projectRepository, times(1)).findById(1L);
    verify(taskMapper, times(1)).toEntity(request);
    verify(taskRepository, times(1)).save(task);
    verify(taskMapper, times(1)).toResponse(savedTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException when project not found on create")
  void create_ShouldThrowNotFound_WhenProjectMissing() {
    // Arrange
    TaskRequestDTO request = TaskMock.createTaskRequestDTO();
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> taskService.create(request));

    assertTrue(exception.getMessage().contains("Project not found by id: 1"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should return task by id when exists")
  void findById_ShouldReturnTask_WhenExists() {
    // Arrange
    Task savedTask = TaskMock.createSavedTaskEntity();
    TaskResponseDTO response = TaskMock.createTaskResponseDTO();

    when(taskRepository.findById(10L)).thenReturn(Optional.of(savedTask));
    when(taskMapper.toResponse(savedTask)).thenReturn(response);

    // Act
    TaskResponseDTO result = taskService.findById(10L);

    // Assert
    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(taskRepository, times(1)).findById(10L);
    verify(taskMapper, times(1)).toResponse(savedTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException when task not found by id")
  void findById_ShouldThrowNotFound_WhenNotExists() {
    // Arrange
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> taskService.findById(99L));

    assertTrue(exception.getMessage().contains("Task not found by id: 99"));
    verify(taskMapper, never()).toResponse(any(Task.class));
  }

  @Test
  @DisplayName("Should return paginated tasks when filter is provided")
  void findByFilter_ShouldReturnPaginatedTasks_WhenFilterProvided() {
    // Arrange
    TaskFilterDTO filter = TaskMock.createTaskFilterDTO();
    Pageable pageable = TaskMock.createPageable(0, 10, "title", "ASC");
    List<Task> tasks = List.of(TaskMock.createSavedTaskEntity());
    Page<Task> taskPage = TaskMock.createTaskPage(tasks, pageable);
    TaskResponseDTO response = TaskMock.createTaskResponseDTO();

    when(taskRepository.findByFilters(Status.TODO, Priority.LOW, 1L, pageable))
        .thenReturn(taskPage);
    when(taskMapper.toResponse(any(Task.class))).thenReturn(response);

    // Act
    PageResponseDTO<TaskResponseDTO> result = taskService.findByFilter(filter);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(0, result.getCurrentPage());
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getTotalElements());

    verify(taskRepository, times(1)).findByFilters(Status.TODO, Priority.LOW, 1L, pageable);
    verify(taskMapper, times(1)).toResponse(any(Task.class));
  }

  @Test
  @DisplayName("Should use default pageable when filter has null fields")
  void findByFilter_ShouldUseDefaultValues_WhenFilterHasNullFields() {
    // Arrange
    TaskFilterDTO filter = TaskMock.createTaskFilterDTOWithNullFields();
    Pageable expectedPageable = TaskMock.createPageable(0, 10, "id", "ASC");
    Page<Task> emptyPage = TaskMock.createEmptyTaskPage(expectedPageable);

    when(taskRepository.findByFilters(null, null, null, expectedPageable)).thenReturn(emptyPage);

    // Act
    PageResponseDTO<TaskResponseDTO> result = taskService.findByFilter(filter);

    // Assert
    assertNotNull(result);
    verify(taskRepository, times(1)).findByFilters(null, null, null, expectedPageable);
  }

  @Test
  @DisplayName("Should update task status when task exists")
  void updateStatus_ShouldUpdate_WhenTaskExists() {
    // Arrange
    Task existingTask = TaskMock.createTaskForUpdateStatus(10L, Status.TODO);
    TaskStatusUpdateDTO dto = TaskMock.createTaskStatusUpdateDTO(Status.DONE);

    when(taskRepository.findById(10L)).thenReturn(Optional.of(existingTask));

    // Act
    taskService.updateStatus(10L, dto);

    // Assert
    assertEquals(Status.DONE, existingTask.getStatus());
    verify(taskRepository, times(1)).save(existingTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException on updateStatus when task not found")
  void updateStatus_ShouldThrowNotFound_WhenTaskNotFound() {
    // Arrange
    TaskStatusUpdateDTO dto = TaskMock.createTaskStatusUpdateDTO(Status.DOING);
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> taskService.updateStatus(99L, dto));

    assertTrue(exception.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should update task priority when task exists")
  void updatePriority_ShouldUpdate_WhenTaskExists() {
    // Arrange
    Task existingTask = TaskMock.createTaskForUpdatePriority(10L, Priority.LOW);
    TaskPriorityUpdateDTO dto = TaskMock.createTaskPriorityUpdateDTO(Priority.HIGH);

    when(taskRepository.findById(10L)).thenReturn(Optional.of(existingTask));

    // Act
    taskService.updatePriority(10L, dto);

    // Assert
    assertEquals(Priority.HIGH, existingTask.getPriority());
    verify(taskRepository, times(1)).save(existingTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException on updatePriority when task not found")
  void updatePriority_ShouldThrowNotFound_WhenTaskNotFound() {
    // Arrange
    TaskPriorityUpdateDTO dto = TaskMock.createTaskPriorityUpdateDTO(Priority.MEDIUM);
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> taskService.updatePriority(99L, dto));

    assertTrue(exception.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should delete task when it exists")
  void delete_ShouldDelete_WhenExists() {
    // Arrange
    when(taskRepository.existsById(10L)).thenReturn(true);

    // Act
    taskService.delete(10L);

    // Assert
    verify(taskRepository, times(1)).deleteById(10L);
  }

  @Test
  @DisplayName("Should throw NotFoundException on delete when task does not exist")
  void delete_ShouldThrowNotFound_WhenNotExists() {
    // Arrange
    when(taskRepository.existsById(99L)).thenReturn(false);

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> taskService.delete(99L));

    assertTrue(exception.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).deleteById(anyLong());
  }
}
