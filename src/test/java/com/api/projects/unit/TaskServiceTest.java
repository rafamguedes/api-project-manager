package com.api.projects.unit;

import com.api.projects.dtos.pagination.PageResponseDTO;
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
import com.api.projects.mappers.TaskMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.repositories.TaskRepository;
import com.api.projects.services.TaskService;
import com.api.projects.services.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
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

  private TaskRequestDTO taskRequestDTO;
  private Task task;
  private Task savedTask;
  private TaskResponseDTO taskResponseDTO;
  private Project project;

  @BeforeEach
  void setUp() {
    project =
        Project.builder()
            .id(1L)
            .name("Project A")
            .description("Desc")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(10))
            .build();

    taskRequestDTO =
        TaskRequestDTO.builder()
            .title("Task 1")
            .description("Desc 1")
            .status(Status.TODO)
            .priority(Priority.LOW)
            .dueDate(LocalDateTime.now().plusDays(2))
            .endDate(LocalDateTime.now().plusDays(3))
            .projectId(1L)
            .build();

    task =
        Task.builder()
            .title("Task 1")
            .description("Desc 1")
            .status(Status.TODO)
            .priority(Priority.LOW)
            .dueDate(LocalDateTime.now().plusDays(2))
            .endDate(LocalDateTime.now().plusDays(3))
            .project(project)
            .build();

    savedTask =
        Task.builder()
            .id(10L)
            .title("Task 1")
            .description("Desc 1")
            .status(Status.TODO)
            .priority(Priority.LOW)
            .dueDate(LocalDateTime.now().plusDays(2))
            .endDate(LocalDateTime.now().plusDays(3))
            .project(project)
            .build();

    taskResponseDTO =
        TaskResponseDTO.builder()
            .id(10L)
            .title("Task 1")
            .description("Desc 1")
            .status(Status.TODO)
            .priority(Priority.LOW)
            .dueDate(savedTask.getDueDate())
            .endDate(savedTask.getEndDate())
            .project(ProjectResponseDTO.builder().build())
            .build();
  }

  @Test
  @DisplayName("Should create task successfully")
  void create_ShouldCreateTask_WhenValidRequest() {
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(taskMapper.toEntity(taskRequestDTO)).thenReturn(task);
    when(taskRepository.save(task)).thenReturn(savedTask);
    when(taskMapper.toResponse(savedTask)).thenReturn(taskResponseDTO);

    TaskResponseDTO result = taskService.create(taskRequestDTO);

    assertNotNull(result);
    assertEquals(taskResponseDTO.getId(), result.getId());
    assertEquals(taskResponseDTO.getTitle(), result.getTitle());
    assertEquals(taskResponseDTO.getStatus(), result.getStatus());

    verify(projectRepository, times(1)).findById(1L);
    verify(taskMapper, times(1)).toEntity(taskRequestDTO);
    verify(taskRepository, times(1)).save(task);
    verify(taskMapper, times(1)).toResponse(savedTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException when project not found on create")
  void create_ShouldThrowNotFound_WhenProjectMissing() {
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> taskService.create(taskRequestDTO));

    assertTrue(ex.getMessage().contains("Project not found by id: 1"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should return task by id when exists")
  void findById_ShouldReturnTask_WhenExists() {
    when(taskRepository.findById(10L)).thenReturn(Optional.of(savedTask));
    when(taskMapper.toResponse(savedTask)).thenReturn(taskResponseDTO);

    TaskResponseDTO result = taskService.findById(10L);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    verify(taskRepository, times(1)).findById(10L);
    verify(taskMapper, times(1)).toResponse(savedTask);
  }

  @Test
  @DisplayName("Should throw NotFoundException when task not found by id")
  void findById_ShouldThrowNotFound_WhenNotExists() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class, () -> taskService.findById(99L));

    assertTrue(ex.getMessage().contains("Task not found by id: 99"));
  }

  @Test
  @DisplayName("Should return paginated tasks when filter is provided")
  void findByFilter_ShouldReturnPaginatedTasks_WhenFilterProvided() {
    TaskFilterDTO filter = new TaskFilterDTO();
    filter.setPage(0);
    filter.setSize(10);
    filter.setSortBy("title");
    filter.setDirection("ASC");
    filter.setStatus(Status.TODO);
    filter.setPriority(Priority.LOW);
    filter.setProjectId(1L);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
    List<Task> tasks = List.of(savedTask);
    Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

    when(taskRepository.findByFilters(Status.TODO, Priority.LOW, 1L, pageable))
        .thenReturn(taskPage);
    when(taskMapper.toResponse(savedTask)).thenReturn(taskResponseDTO);

    PageResponseDTO<TaskResponseDTO> result = taskService.findByFilter(filter);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(taskResponseDTO, result.getContent().get(0));
    assertEquals(0, result.getCurrentPage());
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getTotalElements());

    verify(taskRepository, times(1)).findByFilters(Status.TODO, Priority.LOW, 1L, pageable);
    verify(taskMapper, times(1)).toResponse(savedTask);
  }

  @Test
  @DisplayName("Should return empty page when no tasks found")
  void findByFilter_ShouldReturnEmptyPage_WhenNoTasksFound() {
    TaskFilterDTO filter = new TaskFilterDTO();
    filter.setPage(0);
    filter.setSize(10);
    filter.setSortBy("id");
    filter.setDirection("DESC");

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
    Page<Task> emptyPage = Page.empty(pageable);

    when(taskRepository.findByFilters(null, null, null, pageable)).thenReturn(emptyPage);

    PageResponseDTO<TaskResponseDTO> result = taskService.findByFilter(filter);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    assertEquals(0, result.getTotalElements());
    assertEquals(0, result.getCurrentPage());

    verify(taskRepository, times(1)).findByFilters(null, null, null, pageable);
    verify(taskMapper, never()).toResponse(any(Task.class));
  }

  @Test
  @DisplayName("Should use default pageable when filter has null fields")
  void findByFilter_ShouldUseDefaultValues_WhenFilterHasNullFields() {
    TaskFilterDTO filter = new TaskFilterDTO();

    Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
    Page<Task> emptyPage = Page.empty(expectedPageable);

    when(taskRepository.findByFilters(isNull(), isNull(), isNull(), eq(expectedPageable)))
        .thenReturn(emptyPage);

    PageResponseDTO<TaskResponseDTO> result = taskService.findByFilter(filter);

    assertNotNull(result);
    verify(taskRepository, times(1))
        .findByFilters(isNull(), isNull(), isNull(), eq(expectedPageable));
  }

  @Test
  @DisplayName("Should update task status when task exists")
  void updateStatus_ShouldUpdate_WhenTaskExists() {
    Task existing = Task.builder().id(10L).status(Status.TODO).build();
    TaskStatusUpdateDTO dto = TaskStatusUpdateDTO.builder().status(Status.DONE).build();

    when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));

    taskService.updateStatus(10L, dto);

    assertEquals(Status.DONE, existing.getStatus());
    verify(taskRepository, times(1)).save(existing);
  }

  @Test
  @DisplayName("Should throw NotFoundException on updateStatus when task not found")
  void updateStatus_ShouldThrowNotFound_WhenTaskNotFound() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());
    TaskStatusUpdateDTO dto = TaskStatusUpdateDTO.builder().status(Status.DOING).build();

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> taskService.updateStatus(99L, dto));

    assertTrue(ex.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should update task priority when task exists")
  void updatePriority_ShouldUpdate_WhenTaskExists() {
    Task existing = Task.builder().id(10L).priority(Priority.LOW).build();
    TaskPriorityUpdateDTO dto = TaskPriorityUpdateDTO.builder().priority(Priority.HIGH).build();

    when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));

    taskService.updatePriority(10L, dto);

    assertEquals(Priority.HIGH, existing.getPriority());
    verify(taskRepository, times(1)).save(existing);
  }

  @Test
  @DisplayName("Should throw NotFoundException on updatePriority when task not found")
  void updatePriority_ShouldThrowNotFound_WhenTaskNotFound() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());
    TaskPriorityUpdateDTO dto = TaskPriorityUpdateDTO.builder().priority(Priority.MEDIUM).build();

    NotFoundException ex =
        assertThrows(NotFoundException.class, () -> taskService.updatePriority(99L, dto));

    assertTrue(ex.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Should delete task when it exists")
  void delete_ShouldDelete_WhenExists() {
    when(taskRepository.existsById(10L)).thenReturn(true);

    taskService.delete(10L);

    verify(taskRepository, times(1)).deleteById(10L);
  }

  @Test
  @DisplayName("Should throw NotFoundException on delete when task does not exist")
  void delete_ShouldThrowNotFound_WhenNotExists() {
    when(taskRepository.existsById(99L)).thenReturn(false);

    NotFoundException ex = assertThrows(NotFoundException.class, () -> taskService.delete(99L));

    assertTrue(ex.getMessage().contains("Task not found by id: 99"));
    verify(taskRepository, never()).deleteById(anyLong());
  }
}
