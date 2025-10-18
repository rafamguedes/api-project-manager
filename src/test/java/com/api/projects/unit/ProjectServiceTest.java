package com.api.projects.unit;

import com.api.projects.dtos.pagination.PageResponseDTO;
import com.api.projects.dtos.project.ProjectFilterDTO;
import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.dtos.project.ProjectUpdateRequestDTO;
import com.api.projects.entities.Project;
import com.api.projects.mappers.ProjectMapper;
import com.api.projects.repositories.ProjectRepository;
import com.api.projects.services.ProjectService;
import com.api.projects.unit.mocks.ProjectMock;
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

import java.util.List;
import java.util.Optional;

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
  private ProjectUpdateRequestDTO projectUpdateRequestDTO;

  @BeforeEach
  void setUp() {
    projectRequestDTO = ProjectMock.createProjectRequestDTO();
    project = ProjectMock.createProjectEntity();
    projectResponseDTO = ProjectMock.createProjectResponseDTO();
    savedProject = ProjectMock.createProjectEntity();
    projectUpdateRequestDTO = ProjectMock.createProjectUpdateRequestDTO();
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

  @Test
  @DisplayName("Should return project by ID")
  void findById_ShouldReturnProjectById_WhenProjectExists() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
    when(projectMapper.toResponse(project)).thenReturn(projectResponseDTO);

    // Act
    ProjectResponseDTO result = projectService.findById(projectId);

    // Assert
    assertNotNull(result);
    assertEquals(projectResponseDTO.getId(), result.getId());
    assertEquals(projectResponseDTO.getName(), result.getName());
    assertEquals(projectResponseDTO.getDescription(), result.getDescription());

    verify(projectRepository, times(1)).findById(projectId);
    verify(projectMapper, times(1)).toResponse(project);
  }

  @Test
  @DisplayName("Should throw NotFoundException when project does not exist")
  void findById_ShouldThrowNotFoundException_WhenProjectDoesNotExist() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.empty());

    // Act & Assert
    Exception exception =
        assertThrows(RuntimeException.class, () -> projectService.findById(projectId));

    String expectedMessage = "Project not found with id: " + projectId;
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    verify(projectRepository, times(1)).findById(projectId);
    verify(projectMapper, never()).toResponse(any(Project.class));
  }

  @Test
  @DisplayName("Should update project successfully")
  void updateProject_ShouldUpdateProject_WhenValidRequest() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(projectRepository.save(any(Project.class))).thenReturn(project);

    // Act
    assertDoesNotThrow(() -> projectService.updateProject(projectId, projectUpdateRequestDTO));

    // Assert
    verify(projectRepository, times(1)).findById(projectId);
    verify(projectRepository, times(1)).save(any(Project.class));
  }

  @Test
  @DisplayName("Should throw NotFoundException when updating non-existing project")
  void updateProject_ShouldThrowNotFoundException_WhenProjectDoesNotExist() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> projectService.updateProject(projectId, projectUpdateRequestDTO));

    String expectedMessage = "Project not found with id: " + projectId;
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

    verify(projectRepository, times(1)).findById(projectId);
    verify(projectRepository, never()).save(any(Project.class));
  }

  @Test
  @DisplayName("Should delete project by ID")
  void deleteProjectById_ShouldDeleteProject_WhenProjectExists() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(true);
    doNothing().when(projectRepository).deleteById(projectId);

    // Act
    assertDoesNotThrow(() -> projectService.deleteProjectById(projectId));

    // Assert
    verify(projectRepository, times(1)).existsById(projectId);
    verify(projectRepository, times(1)).deleteById(projectId);
  }

  @Test
  @DisplayName("Should throw NotFoundException when deleting non-existing project")
  void deleteProjectById_ShouldThrowNotFoundException_WhenProjectDoesNotExist() {
    // Arrange
    Long projectId = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(false);

    // Act & Assert
    Exception exception =
        assertThrows(RuntimeException.class, () -> projectService.deleteProjectById(projectId));
    String expectedMessage = "Project not found with id: " + projectId;
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verify(projectRepository, times(1)).existsById(projectId);
    verify(projectRepository, never()).deleteById(projectId);
  }

  @Test
  @DisplayName("Should delete multiple projects by IDs")
  void deleteProjectsByIds_ShouldDeleteMultipleProjects_WhenProjectsExist() {
    // Arrange
    List<Long> projectIds = List.of(1L, 2L, 3L);

    doNothing().when(projectRepository).deleteAllByIdInBatch(projectIds);

    // Act
    assertDoesNotThrow(() -> projectService.deleteProjectsByIds(projectIds));

    // Assert
    verify(projectRepository, times(1)).deleteAllByIdInBatch(projectIds);
  }
}
