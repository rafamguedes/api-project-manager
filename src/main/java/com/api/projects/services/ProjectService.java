package com.api.projects.services;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectFilterDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.project.ProjectUpdateRequestDTO;
import com.api.projects.entities.Project;
import com.api.projects.mappers.ProjectMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private static final String PROJECT_NOT_FOUND_MESSAGE = "Project not found with id: ";

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;

  public ProjectResponseDTO create(ProjectRequestDTO request) {
    Project project = projectMapper.toEntity(request);
    Project savedProject = projectRepository.save(project);

    return projectMapper.toResponse(savedProject);
  }

  public PageResponseDTO<ProjectResponseDTO> findByFilter(ProjectFilterDTO request) {
    Pageable pageable =
        PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

    Page<ProjectResponseDTO> pageResult =
        projectRepository.findAll(pageable).map(projectMapper::toResponse);

    return PageResponseDTO.of(pageResult);
  }

  public ProjectResponseDTO findById(Long id) {
    return projectRepository
        .findById(id)
        .map(projectMapper::toResponse)
        .orElseThrow(() -> new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + id));
  }

  public void updateProject(Long id, ProjectUpdateRequestDTO request) {
    Project existingProject =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + id));

    Optional.ofNullable(request.getName()).ifPresent(existingProject::setName);
    Optional.ofNullable(request.getDescription()).ifPresent(existingProject::setDescription);
    Optional.ofNullable(request.getStartDate()).ifPresent(existingProject::setStartDate);
    Optional.ofNullable(request.getEndDate()).ifPresent(existingProject::setEndDate);

    projectRepository.save(existingProject);
  }

  public void deleteProjectById(Long id) {
    if (!projectRepository.existsById(id)) {
      throw new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + id);
    }

    projectRepository.deleteById(id);
  }

  public void deleteProjectsByIds(List<Long> ids) {
    projectRepository.deleteAllByIdInBatch(ids);
  }
}
