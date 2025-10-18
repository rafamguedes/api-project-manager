package com.api.projects.unit.mocks;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectFilterDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.entities.Project;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectMock {

  // ProjectRequestDTO Mocks
  public static ProjectRequestDTO createProjectRequestDTO() {
    return ProjectRequestDTO.builder()
        .name("Test Project")
        .description("Test Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  public static ProjectRequestDTO createProjectRequestDTO(String name, String description) {
    return ProjectRequestDTO.builder()
        .name(name)
        .description(description)
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  public static ProjectRequestDTO createInvalidProjectRequestDTO() {
    return ProjectRequestDTO.builder()
        .name("") // Empty name - invalid
        .description("") // Empty description
        .build();
  }

  // Project Entity Mocks
  public static Project createProjectEntity() {
    return Project.builder()
        .id(1L)
        .name("Test Project")
        .description("Test Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  public static Project createProjectEntity(Long id, String name) {
    return Project.builder()
        .id(id)
        .name(name)
        .description("Description for " + name)
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  public static Project createProjectEntityWithoutId() {
    return Project.builder()
        .name("Test Project")
        .description("Test Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  // ProjectResponseDTO Mocks
  public static ProjectResponseDTO createProjectResponseDTO() {
    return ProjectResponseDTO.builder()
        .id(1L)
        .name("Test Project")
        .description("Test Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  public static ProjectResponseDTO createProjectResponseDTO(Long id, String name) {
    return ProjectResponseDTO.builder()
        .id(id)
        .name(name)
        .description("Description for " + name)
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(30))
        .build();
  }

  // ProjectFilterDTO Mocks
  public static ProjectFilterDTO createProjectFilterDTO() {
    ProjectFilterDTO filter = new ProjectFilterDTO();
    filter.setPage(0);
    filter.setSize(10);
    filter.setSortBy("name");
    filter.setDirection("ASC");
    return filter;
  }

  public static ProjectFilterDTO createProjectFilterDTO(
      Integer page, Integer size, String sortBy, String direction) {
    ProjectFilterDTO filter = new ProjectFilterDTO();
    filter.setPage(page);
    filter.setSize(size);
    filter.setSortBy(sortBy);
    filter.setDirection(direction);
    return filter;
  }

  public static ProjectFilterDTO createProjectFilterDTOWithNullFields() {
    return new ProjectFilterDTO(); // Uses default values
  }

  // Page and Pagination Mocks
  public static Page<Project> createProjectPage(List<Project> projects, Pageable pageable) {
    return new PageImpl<>(projects, pageable, projects.size());
  }

  public static Page<Project> createEmptyProjectPage(Pageable pageable) {
    return Page.empty(pageable);
  }

  public static Pageable createPageable(
      Integer page, Integer size, String sortBy, String direction) {
    return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
  }

  public static PageResponseDTO<ProjectResponseDTO> createProjectPageResponseDTO(
      List<ProjectResponseDTO> content) {
    return PageResponseDTO.of(new PageImpl<>(content, PageRequest.of(0, 10), content.size()));
  }

  // Bulk Data Mocks
  public static List<Project> createMultipleProjects() {
    return List.of(
        createProjectEntity(1L, "Project One"),
        createProjectEntity(2L, "Project Two"),
        createProjectEntity(3L, "Project Three"));
  }

  public static List<ProjectResponseDTO> createMultipleProjectResponseDTOs() {
    return List.of(
        createProjectResponseDTO(1L, "Project One"),
        createProjectResponseDTO(2L, "Project Two"),
        createProjectResponseDTO(3L, "Project Three"));
  }

  // Specific Scenario Mocks
  public static ProjectRequestDTO createProjectRequestForDifferentScenarios() {
    return ProjectRequestDTO.builder()
        .name("Scenario Project")
        .description("Scenario Description")
        .startDate(LocalDateTime.now().plusDays(1))
        .endDate(LocalDateTime.now().plusDays(60))
        .build();
  }

  public static Project createSavedProjectEntity() {
    return Project.builder()
        .id(99L)
        .name("Saved Project")
        .description("Saved Description")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(45))
        .build();
  }
}
