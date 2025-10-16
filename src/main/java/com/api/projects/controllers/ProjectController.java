package com.api.projects.controllers;

import com.api.projects.dtos.PageResponseDTO;
import com.api.projects.dtos.ProjectFilterDTO;
import com.api.projects.dtos.ProjectRequestDTO;
import com.api.projects.dtos.ProjectResponseDTO;
import com.api.projects.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
public class ProjectController {
  private final ProjectService projectService;

  @PostMapping
  @Operation(summary = "Create Project", description = "Create a new project")
  public ResponseEntity<ProjectResponseDTO> createProject(
      @Valid @RequestBody ProjectRequestDTO request) {
    var response = projectService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  @Operation(
      summary = "List Projects",
      description = "Retrieve a paginated list of projects with optional filtering")
  public ResponseEntity<PageResponseDTO<ProjectResponseDTO>> listProjects(
      @Valid ProjectFilterDTO filter) {
    var response = projectService.findByFilter(filter);
    return ResponseEntity.ok(response);
  }
}
