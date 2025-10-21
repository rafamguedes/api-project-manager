package com.api.projects.services;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectFilterDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.project.ProjectUpdateRequestDTO;
import com.api.projects.entities.Project;
import com.api.projects.entities.User;
import com.api.projects.mappers.ProjectMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.exceptions.NotFoundException;
import com.api.projects.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

  private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";
  private static final String PROJECT_NOT_FOUND_MESSAGE = "Project not found with id: ";

  private static final String PROJECT_CACHE = "project";
  private static final String PROJECTS_CACHE = "projects";

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ProjectMapper projectMapper;

  @CacheEvict(value = PROJECTS_CACHE, allEntries = true)
  public ProjectResponseDTO create(ProjectRequestDTO request) {
    log.debug("Creating new project and evicting projects cache");

    User user =
        userRepository
            .findById(request.getOwnerId())
            .orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_MESSAGE + request.getOwnerId()));

    Project project = projectMapper.toEntity(request);
    project.setOwner(user);

    Project savedProject = projectRepository.save(project);
    return projectMapper.toResponse(savedProject);
  }

  @Cacheable(
      value = PROJECTS_CACHE,
      key =
          "{#request?.page ?: 0, #request?.size ?: 20, #request?.sortBy ?: 'id', #request?.direction ?: 'ASC'}")
  public PageResponseDTO<ProjectResponseDTO> findByFilter(ProjectFilterDTO request) {
    log.debug("Fetching projects from database with filter: {}", request);
    Pageable pageable =
        PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

    Page<ProjectResponseDTO> pageResult =
        projectRepository.findAll(pageable).map(projectMapper::toResponse);

    return PageResponseDTO.of(pageResult);
  }

  @Cacheable(value = PROJECT_CACHE, key = "#id")
  public ProjectResponseDTO findById(Long id) {
    log.debug("Fetching project from database with id: {}", id);
    return projectRepository
        .findById(id)
        .map(projectMapper::toResponse)
        .orElseThrow(() -> new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + id));
  }

  @Caching(
      evict = {
        @CacheEvict(value = PROJECT_CACHE, key = "#id"),
        @CacheEvict(value = PROJECTS_CACHE, allEntries = true)
      })
  public void updateProject(Long id, ProjectUpdateRequestDTO request) {
    log.debug("Updating project with id: {} and evicting specific cache", id);
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

  @Caching(
      evict = {
        @CacheEvict(value = PROJECT_CACHE, key = "#id"),
        @CacheEvict(value = PROJECTS_CACHE, allEntries = true)
      })
  public void deleteProjectById(Long id) {
    log.debug("Deleting project with id: {} and evicting caches", id);
    if (!projectRepository.existsById(id)) {
      throw new NotFoundException(PROJECT_NOT_FOUND_MESSAGE + id);
    }
    projectRepository.deleteById(id);
  }

  @CacheEvict(
      value = {PROJECT_CACHE, PROJECTS_CACHE},
      allEntries = true)
  public void deleteProjectsByIds(List<Long> ids) {
    log.debug("Deleting multiple projects and evicting all caches");
    projectRepository.deleteAllByIdInBatch(ids);
  }
}
