package com.api.projects.services;

import com.api.projects.dtos.PageResponseDTO;
import com.api.projects.dtos.ProjectFilterDTO;
import com.api.projects.dtos.ProjectRequestDTO;
import com.api.projects.dtos.ProjectResponseDTO;
import com.api.projects.entities.Project;
import com.api.projects.mappers.ProjectMapper;
import com.api.projects.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
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
}
