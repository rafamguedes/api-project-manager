package com.api.projects.controllers;

import com.api.projects.dtos.PageResponseDTO;
import com.api.projects.dtos.ProjectFilterDTO;
import com.api.projects.dtos.ProjectRequestDTO;
import com.api.projects.dtos.ProjectResponseDTO;
import com.api.projects.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {
  private final ProjectService projectService;

  @PostMapping
  public ResponseEntity<ProjectResponseDTO> createProject(
      @Valid @RequestBody ProjectRequestDTO request) {
    var response = projectService.create(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<PageResponseDTO<ProjectResponseDTO>> listProjects(
      @Valid ProjectFilterDTO filter) {
    var response = projectService.findByFilter(filter);
    return ResponseEntity.ok(response);
  }
}
