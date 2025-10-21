package com.api.projects.integration.mocks;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.unit.mocks.UserMock;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectMock {

  public static ProjectRequestDTO createProjectRequestDTO() {
    return ProjectRequestDTO.builder()
        .name("Integration Test Project")
        .description("Project for integration testing")
        .startDate(LocalDateTime.parse("2390-10-17T18:00:00"))
        .endDate(LocalDateTime.parse("2390-10-17T18:00:00").plusDays(30))
        .ownerId(1L)
        .build();
  }

  public static ProjectResponseDTO createProjectResponseDTO() {
    return ProjectResponseDTO.builder()
        .id(1L)
        .name("Integration Test Project")
        .description("Project for integration testing")
        .startDate(LocalDateTime.parse("2390-10-17T18:00:00"))
        .endDate(LocalDateTime.parse("2390-10-17T18:00:00").plusDays(30))
        .createdAt(LocalDateTime.parse("2390-10-17T18:00:00"))
        .updatedAt(LocalDateTime.parse("2390-10-17T18:00:00").plusHours(1))
        .createdBy("testuser")
        .updatedBy("testuser")
        .owner(UserMock.createUserResponseDTO())
        .build();
  }

  public static PageResponseDTO<ProjectResponseDTO> createProjectPageResponseDTO() {
    return new PageResponseDTO<>(List.of(createProjectResponseDTO()), 0, 1, 1, 10, true, true);
  }
}
