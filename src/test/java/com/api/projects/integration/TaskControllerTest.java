package com.api.projects.integration;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.task.TaskPriorityUpdateDTO;
import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.dtos.task.TaskStatusUpdateDTO;
import com.api.projects.enums.Priority;
import com.api.projects.enums.Status;
import com.api.projects.integration.mocks.TaskMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest extends BaseIntegration {

  private static final String TASK_BASE_URL = "/api/v1/tasks";

  private TaskRequestDTO taskRequestDTO;
  private TaskResponseDTO taskResponseDTO;
  private PageResponseDTO<TaskResponseDTO> pageResponseDTO;

  @BeforeEach
  void setUp() {
    taskRequestDTO = TaskMock.createTaskRequestDTO();
    taskResponseDTO = TaskMock.createTaskResponseDTO();
    pageResponseDTO = TaskMock.createTaskPageResponseDTO();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("POST /api/v1/tasks - Should create task and return 201")
  void create_ShouldReturnCreatedTask_WhenValidRequest() throws Exception {
    when(taskService.create(any(TaskRequestDTO.class))).thenReturn(taskResponseDTO);

    mockMvc
        .perform(
            post(TASK_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequestDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(10)))
        .andExpect(jsonPath("$.title", is("Integration Task")))
        .andExpect(jsonPath("$.description", is("Task for integration testing")))
        .andExpect(jsonPath("$.status", is("TODO")))
        .andExpect(jsonPath("$.priority", is("MEDIUM")))
        .andExpect(jsonPath("$.dueDate", is("2390-10-20T09:00:00")))
        .andExpect(jsonPath("$.project.id", is(1)))
        .andExpect(jsonPath("$.createdAt", is("2390-10-10T10:00:00")))
        .andExpect(jsonPath("$.updatedAt", is("2390-10-11T11:00:00")))
        .andExpect(jsonPath("$.createdBy", is("testuser")))
        .andExpect(jsonPath("$.updatedBy", is("testuser")));

    verify(taskService, times(1)).create(any(TaskRequestDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("POST /api/v1/tasks - Should return 400 when validation fails")
  void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
    TaskRequestDTO invalidRequest = TaskRequestDTO.builder().title("").projectId(null).build();

    mockMvc
        .perform(
            post(TASK_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());

    verify(taskService, never()).create(any(TaskRequestDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/tasks/{id} - Should return task by id")
  void getById_ShouldReturnTask_WhenExists() throws Exception {
    when(taskService.findById(10L)).thenReturn(taskResponseDTO);

    mockMvc
        .perform(get(TASK_BASE_URL + "/{id}", 10L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(10)))
        .andExpect(jsonPath("$.title", is("Integration Task")))
        .andExpect(jsonPath("$.project.id", is(1)))
        .andExpect(jsonPath("$.createdAt", is("2390-10-10T10:00:00")))
        .andExpect(jsonPath("$.updatedAt", is("2390-10-11T11:00:00")))
        .andExpect(jsonPath("$.createdBy", is("testuser")))
        .andExpect(jsonPath("$.updatedBy", is("testuser")));

    verify(taskService, times(1)).findById(10L);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/tasks - Should return paginated tasks")
  void findByFilter_ShouldReturnPaginatedTasks_WhenValidFilter() throws Exception {
    when(taskService.findByFilter(any())).thenReturn(pageResponseDTO);

    mockMvc
        .perform(
            get(TASK_BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "title")
                .param("direction", "ASC")
                .param("status", "TODO")
                .param("priority", "MEDIUM")
                .param("projectId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", is(10)))
        .andExpect(jsonPath("$.content[0].title", is("Integration Task")))
        .andExpect(jsonPath("$.currentPage", is(0)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(1)));

    verify(taskService, times(1)).findByFilter(any());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/tasks - Should return empty page when no tasks")
  void findByFilter_ShouldReturnEmptyPage_WhenNoTasks() throws Exception {
    PageResponseDTO<TaskResponseDTO> emptyPage =
        new PageResponseDTO<>(List.of(), 0, 0, 0, 10, true, true);
    when(taskService.findByFilter(any())).thenReturn(emptyPage);

    mockMvc
        .perform(get(TASK_BASE_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));

    verify(taskService, times(1)).findByFilter(any());
  }

  @ParameterizedTest
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/tasks - Should handle different filter parameters")
  @CsvSource({"0,5,title,ASC", "1,20,dueDate,DESC", "2,50,id,ASC"})
  void findByFilter_ShouldHandleDifferentParameters(
      String page, String size, String sortBy, String direction) throws Exception {
    when(taskService.findByFilter(any())).thenReturn(pageResponseDTO);

    mockMvc
        .perform(
            get(TASK_BASE_URL)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .param("direction", direction))
        .andExpect(status().isOk());

    verify(taskService, times(1)).findByFilter(any());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("PUT /api/v1/tasks/{id}/status - Should update status and return 204")
  void updateStatus_ShouldReturnNoContent_WhenValid() throws Exception {
    TaskStatusUpdateDTO dto = TaskStatusUpdateDTO.builder().status(Status.DONE).build();

    mockMvc
        .perform(
            put(TASK_BASE_URL + "/{id}/status", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNoContent());

    verify(taskService, times(1)).updateStatus(eq(10L), any(TaskStatusUpdateDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("PUT /api/v1/tasks/{id}/priority - Should update priority and return 204")
  void updatePriority_ShouldReturnNoContent_WhenValid() throws Exception {
    TaskPriorityUpdateDTO dto = TaskPriorityUpdateDTO.builder().priority(Priority.HIGH).build();

    mockMvc
        .perform(
            put(TASK_BASE_URL + "/{id}/priority", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNoContent());

    verify(taskService, times(1)).updatePriority(eq(10L), any(TaskPriorityUpdateDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("DELETE /api/v1/tasks/{id} - Should delete and return 204")
  void delete_ShouldReturnNoContent_WhenValid() throws Exception {
    mockMvc.perform(delete(TASK_BASE_URL + "/{id}", 10L)).andExpect(status().isNoContent());

    verify(taskService, times(1)).delete(10L);
  }
}
