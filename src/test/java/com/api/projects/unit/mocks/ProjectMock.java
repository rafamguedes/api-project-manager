package com.api.projects.unit.mocks;

import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.entities.Project;

import java.time.LocalDateTime;

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
}
