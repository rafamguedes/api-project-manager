package com.api.projects.integration;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.integration.mocks.ProjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest extends BaseIntegration {

  private static final String PROJECT_BASE_URL = "/api/v1/projects";

  private ProjectRequestDTO projectRequestDTO;
  private ProjectResponseDTO projectResponseDTO;
  private PageResponseDTO<ProjectResponseDTO> pageResponseDTO;

  @BeforeEach
  void setUp() {
    projectRequestDTO = ProjectMock.createProjectRequestDTO();
    projectResponseDTO = ProjectMock.createProjectResponseDTO();
    pageResponseDTO = ProjectMock.createProjectPageResponseDTO();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("POST /api/v1/projects - Should create project and return 201")
  void create_ShouldReturnCreatedProject_WhenValidRequest() throws Exception {
    // Arrange
    when(projectService.create(any(ProjectRequestDTO.class))).thenReturn(projectResponseDTO);

    // Act & Assert
    String responseContent =
        mockMvc
            .perform(
                post(PROJECT_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(projectRequestDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Convert response to DTO
    ProjectResponseDTO responseDto =
        objectMapper.readValue(responseContent, ProjectResponseDTO.class);

    assertThat(responseDto).isEqualTo(projectResponseDTO);
    assertThat(responseDto.getId()).isEqualTo(projectResponseDTO.getId());
    assertThat(responseDto.getName()).isEqualTo(projectResponseDTO.getName());

    verify(projectService, times(1)).create(any(ProjectRequestDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
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
            post(PROJECT_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());

    verify(projectService, never()).create(any(ProjectRequestDTO.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/projects - Should return paginated projects")
  void findAllByFilter_ShouldReturnPaginatedProjects_WhenValidFilter() throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc
        .perform(
            get(PROJECT_BASE_URL)
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
    ;

    verify(projectService, times(1)).findByFilter(any());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/projects - Should return empty page when no projects")
  void findAllByFilter_ShouldReturnEmptyPage_WhenNoProjects() throws Exception {
    // Arrange
    PageResponseDTO<ProjectResponseDTO> emptyPage =
        new PageResponseDTO<>(List.of(), 0, 0, 0, 10, true, true);
    when(projectService.findByFilter(any())).thenReturn(emptyPage);

    // Act & Assert
    mockMvc
        .perform(get(PROJECT_BASE_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));

    verify(projectService, times(1)).findByFilter(any());
  }

  @ParameterizedTest
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/projects - Should handle different filter parameters")
  @CsvSource({"0, 5, name, ASC", "1, 20, createdAt, DESC", "2, 50, id, ASC"})
  void findAllByFilter_ShouldHandleDifferentParameters(
      String page, String size, String sortBy, String direction) throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc
        .perform(
            get(PROJECT_BASE_URL)
                .param("page", page)
                .param("size", size)
                .param("sortBy", sortBy)
                .param("direction", direction))
        .andExpect(status().isOk());

    verify(projectService, times(1)).findByFilter(any());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/projects - Should use default values when no parameters")
  void findAllByFilter_ShouldUseDefaultValues_WhenNoParameters() throws Exception {
    // Arrange
    when(projectService.findByFilter(any())).thenReturn(pageResponseDTO);

    // Act & Assert
    mockMvc.perform(get(PROJECT_BASE_URL)).andExpect(status().isOk());

    verify(projectService, times(1)).findByFilter(any());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  @DisplayName("GET /api/v1/projects/{id} - Should return project by ID")
  void getProjectById_ShouldReturnProject_WhenProjectExists() throws Exception {
    // Arrange
    Long projectId = 1L;
    when(projectService.findById(projectId)).thenReturn(projectResponseDTO);

    // Act & Assert
    mockMvc
        .perform(get(PROJECT_BASE_URL + "/{id}", projectId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("Integration Test Project")))
        .andExpect(jsonPath("$.description", is("Project for integration testing")))
        .andExpect(jsonPath("$.startDate", is("2390-10-17T18:00:00")))
        .andExpect(jsonPath("$.endDate", is("2390-11-16T18:00:00")))
        .andExpect(jsonPath("$.createdAt", is("2390-10-17T18:00:00")))
        .andExpect(jsonPath("$.updatedAt", is("2390-10-17T19:00:00")))
        .andExpect(jsonPath("$.createdBy", is("testuser")))
        .andExpect(jsonPath("$.updatedBy", is("testuser")));

    verify(projectService, times(1)).findById(projectId);
  }

  @Test
  @WithMockUser(username = "adminuser", roles = "ADMIN")
  @DisplayName("PUT /api/v1/projects/{id} - Should update project and return 204")
  void updateProject_ShouldReturnNoContent_WhenProjectUpdated() throws Exception {
    // Arrange
    Long projectId = 1L;
    doNothing().when(projectService).updateProject(eq(projectId), any());

    // Act & Assert
    mockMvc
        .perform(
            put(PROJECT_BASE_URL + "/{id}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDTO)))
        .andExpect(status().isNoContent());

    verify(projectService, times(1)).updateProject(eq(projectId), any());
  }

  @Test
  @WithMockUser(username = "adminuser", roles = "ADMIN")
  @DisplayName("DELETE /api/v1/projects/{id} - Should delete project and return 204")
  void deleteProjectById_ShouldReturnNoContent_WhenProjectDeleted() throws Exception {
    // Arrange
    Long projectId = 1L;

    doNothing().when(projectService).deleteProjectById(projectId);

    // Act & Assert
    mockMvc
        .perform(delete(PROJECT_BASE_URL + "/{id}", projectId))
        .andExpect(status().isNoContent());

    verify(projectService, times(1)).deleteProjectById(projectId);
  }

  @Test
  @WithMockUser(username = "adminuser", roles = "ADMIN")
  @DisplayName(
      "POST /api/v1/projects/delete-by-ids - Should delete multiple projects and return 204")
  void deleteProjectsByIds_ShouldReturnNoContent_WhenProjectsDeleted() throws Exception {
    // Arrange
    List<Long> projectIds = List.of(1L, 2L, 3L);

    doNothing().when(projectService).deleteProjectsByIds(projectIds);

    // Act & Assert
    mockMvc
        .perform(
            post(PROJECT_BASE_URL + "/delete-by-ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectIds)))
        .andExpect(status().isNoContent());

    verify(projectService, times(1)).deleteProjectsByIds(projectIds);
  }
}
