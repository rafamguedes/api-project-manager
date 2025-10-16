package com.api.projects.unit;

import com.api.projects.dtos.PageResponseDTO;
import com.api.projects.dtos.ProjectFilterDTO;
import com.api.projects.dtos.ProjectRequestDTO;
import com.api.projects.dtos.ProjectResponseDTO;
import com.api.projects.entities.Project;
import com.api.projects.mappers.ProjectMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @Mock private ProjectRepository projectRepository;

  @Mock private ProjectMapper projectMapper;

  @InjectMocks private ProjectService projectService;

  private ProjectRequestDTO projectRequestDTO;
  private Project project;
  private ProjectResponseDTO projectResponseDTO;
  private Project savedProject;

  @BeforeEach
  void setUp() {
    projectRequestDTO =
        ProjectRequestDTO.builder()
            .name("Test Project")
            .description("Test Description")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(30))
            .build();

    project =
        Project.builder()
            .id(1L)
            .name("Test Project")
            .description("Test Description")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(30))
            .build();

    projectResponseDTO =
        ProjectResponseDTO.builder()
            .id(1L)
            .name("Test Project")
            .description("Test Description")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(30))
            .build();

    savedProject =
        Project.builder()
            .id(1L)
            .name("Test Project")
            .description("Test Description")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(30))
            .build();
  }

  @Test
  @DisplayName("Should create project successfully")
  void create_ShouldCreateProject_WhenValidRequest() {
    // Arrange
    when(projectMapper.toEntity(projectRequestDTO)).thenReturn(project);
    when(projectRepository.save(project)).thenReturn(savedProject);
    when(projectMapper.toResponse(savedProject)).thenReturn(projectResponseDTO);

    // Act
    ProjectResponseDTO result = projectService.create(projectRequestDTO);

    // Assert
    assertNotNull(result);
    assertEquals(projectResponseDTO.getId(), result.getId());
    assertEquals(projectResponseDTO.getName(), result.getName());
    assertEquals(projectResponseDTO.getDescription(), result.getDescription());

    verify(projectMapper, times(1)).toEntity(projectRequestDTO);
    verify(projectRepository, times(1)).save(project);
    verify(projectMapper, times(1)).toResponse(savedProject);
  }

  @Test
  @DisplayName("Should return paginated projects when filter is provided")
  void findAllByFilter_ShouldReturnPaginatedProjects_WhenFilterProvided() {
    // Arrange
    ProjectFilterDTO filterDTO = new ProjectFilterDTO();
    filterDTO.setPage(0);
    filterDTO.setSize(10);
    filterDTO.setSortBy("name");
    filterDTO.setDirection("ASC");

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
    List<Project> projects = List.of(project);
    Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());

    when(projectRepository.findAll(pageable)).thenReturn(projectPage);
    when(projectMapper.toResponse(project)).thenReturn(projectResponseDTO);

    // Act
    PageResponseDTO<ProjectResponseDTO> result = projectService.findByFilter(filterDTO);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(projectResponseDTO, result.getContent().get(0));
    assertEquals(0, result.getCurrentPage());
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getTotalElements());

    verify(projectRepository, times(1)).findAll(pageable);
    verify(projectMapper, times(1)).toResponse(project);
  }

  @Test
  @DisplayName("Should return empty page when no projects found")
  void findAllByFilter_ShouldReturnEmptyPage_WhenNoProjectsFound() {
    // Arrange
    ProjectFilterDTO filterDTO = new ProjectFilterDTO();
    filterDTO.setPage(0);
    filterDTO.setSize(10);
    filterDTO.setSortBy("id");
    filterDTO.setDirection("DESC");

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
    Page<Project> emptyPage = Page.empty(pageable);

    when(projectRepository.findAll(pageable)).thenReturn(emptyPage);

    // Act
    PageResponseDTO<ProjectResponseDTO> result = projectService.findByFilter(filterDTO);

    // Assert
    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
    assertEquals(0, result.getTotalElements());
    assertEquals(0, result.getCurrentPage());

    verify(projectRepository, times(1)).findAll(pageable);
    verify(projectMapper, never()).toResponse(any(Project.class));
  }

  @Test
  @DisplayName("Should use default values when filter has null fields")
  void findAllByFilter_ShouldUseDefaultValues_WhenFilterHasNullFields() {
    // Arrange
    ProjectFilterDTO filterDTO = new ProjectFilterDTO();

    Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
    Page<Project> emptyPage = Page.empty(expectedPageable);

    when(projectRepository.findAll(expectedPageable)).thenReturn(emptyPage);

    // Act
    PageResponseDTO<ProjectResponseDTO> result = projectService.findByFilter(filterDTO);

    // Assert
    assertNotNull(result);
    verify(projectRepository, times(1)).findAll(expectedPageable);
  }
}
