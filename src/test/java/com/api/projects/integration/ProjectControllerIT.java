package com.api.projects.integration;

import com.api.projects.controllers.ProjectController;
import com.api.projects.dtos.PageResponseDTO;
import com.api.projects.dtos.ProjectRequestDTO;
import com.api.projects.dtos.ProjectResponseDTO;
import com.api.projects.services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerIT {

  private static final String BASE_URL = "/api/v1/projects";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ProjectService projectService;

  private ProjectRequestDTO projectRequestDTO;
  private ProjectResponseDTO projectResponseDTO;
  private PageResponseDTO<ProjectResponseDTO> pageResponseDTO;

  @BeforeEach
  void setUp() {
    projectRequestDTO =
        ProjectRequestDTO.builder()
            .name("Integration Test Project")
            .description("Project for integration testing")
            .startDate(LocalDateTime.parse("2390-10-17T18:00:00"))
            .endDate(LocalDateTime.parse("2390-10-17T18:00:00").plusDays(30))
            .build();

    projectResponseDTO =
        ProjectResponseDTO.builder()
            .id(1L)
            .name("Integration Test Project")
            .description("Project for integration testing")
            .startDate(LocalDateTime.parse("2390-10-17T18:00:00"))
            .endDate(LocalDateTime.parse("2390-10-17T18:00:00").plusDays(30))
            .build();

    pageResponseDTO = new PageResponseDTO<>(List.of(projectResponseDTO), 0, 1, 1, 10, true, true);
  }

  @Test
  @DisplayName("POST /api/v1/projects - Should create project and return 201")
  void create_ShouldReturnCreatedProject_WhenValidRequest() throws Exception {
    // Arrange
    when(projectService.create(any(ProjectRequestDTO.class))).thenReturn(projectResponseDTO);

    // Act & Assert
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Integration Test Project")))
        .andExpect(jsonPath("$.description", is("Project for integration testing")))
        .andExpect(jsonPath("$.startDate", is("2390-10-17T18:00:00")))
        .andExpect(jsonPath("$.endDate", is("2390-11-16T18:00:00")));

    verify(projectService, times(1)).create(any(ProjectRequestDTO.class));
  }

  @Test
  @DisplayName("POST /api/v1/projects - Should return 400 when validation fails")
  void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
    // Arrange
    ProjectRequestDTO invalidRequest =
        ProjectRequestDTO.builder()
            .name("") // Empty name - should fail validation
            .build();

    // Act & Assert
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());

    verify(projectService, never()).create(any(ProjectRequestDTO.class));
  }

  @Test
  @DisplayName("GET /api/v1/projects - Should return paginated projects")
  void findAllByFilter_ShouldReturnPaginatedProjects_WhenValidFilter() throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc
        .perform(
            get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("direction", "ASC"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].id", is(1)))
        .andExpect(jsonPath("$.content[0].name", is("Integration Test Project")))
        .andExpect(jsonPath("$.currentPage", is(0)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(1)));

    verify(projectService, times(1)).findByFilter(any());
  }

  @Test
  @DisplayName("GET /api/v1/projects - Should return empty page when no projects")
  void findAllByFilter_ShouldReturnEmptyPage_WhenNoProjects() throws Exception {
    // Arrange
    PageResponseDTO<ProjectResponseDTO> emptyPage =
        new PageResponseDTO<>(List.of(), 0, 0, 0, 10, true, true);
    when(projectService.findByFilter(any())).thenReturn(emptyPage);

    // Act & Assert
    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));

    verify(projectService, times(1)).findByFilter(any());
  }

  @ParameterizedTest
  @DisplayName("GET /api/v1/projects - Should handle different filter parameters")
  @CsvSource({"0, 5, name, ASC", "1, 20, createdAt, DESC", "2, 50, id, ASC"})
  void findAllByFilter_ShouldHandleDifferentParameters(
      String page, String size, String sortBy, String direction) throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc
        .perform(
            get(BASE_URL)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .param("direction", direction))
        .andExpect(status().isOk());

    verify(projectService, times(1)).findByFilter(any());
  }

  @Test
  @DisplayName("GET /api/v1/projects - Should use default values when no parameters")
  void findAllByFilter_ShouldUseDefaultValues_WhenNoParameters() throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());

    verify(projectService, times(1)).findByFilter(any());
  }
}
