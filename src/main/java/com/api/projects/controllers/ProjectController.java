package com.api.projects.controllers;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectFilterDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.project.ProjectUpdateRequestDTO;
import com.api.projects.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
  private final ProjectService projectService;

  @PostMapping
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Create Project", description = "Create a new project")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Project created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
      })
  public ResponseEntity<ProjectResponseDTO> createProject(
      @Valid @RequestBody ProjectRequestDTO request) {
    var response = projectService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(
      summary = "List Projects",
      description = "Retrieve a paginated list of projects with optional filtering")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
      })
  public ResponseEntity<PageResponseDTO<ProjectResponseDTO>> listProjects(
      @Valid ProjectFilterDTO filter) {
    var response = projectService.findByFilter(filter);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @Operation(summary = "Get Project by ID", description = "Retrieve a project by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Project found"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
      })
  public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
    ProjectResponseDTO response = projectService.findById(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @Operation(summary = "Update Project", description = "Update an existing project by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Project updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Project not found")
      })
  public ResponseEntity<Void> updateProject(
      @PathVariable Long id, @Valid @RequestBody ProjectUpdateRequestDTO request) {
    projectService.updateProject(id, request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @Operation(summary = "Delete Project by ID", description = "Delete a project by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Project not found")
      })
  public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
    projectService.deleteProjectById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/delete-by-ids")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @Operation(
      summary = "Delete Projects by IDs",
      description = "Delete multiple projects by their IDs")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Projects deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
      })
  public ResponseEntity<Void> deleteProjectsByIds(@Valid @RequestBody List<Long> ids) {
    projectService.deleteProjectsByIds(ids);
    return ResponseEntity.noContent().build();
  }
}
