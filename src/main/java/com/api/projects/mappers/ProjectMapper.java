package com.api.projects.mappers;

import com.api.projects.dtos.project.ProjectRequestDTO;
import com.api.projects.dtos.project.ProjectResponseDTO;
import com.api.projects.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tasks", ignore = true)
  Project toEntity(ProjectRequestDTO projectRequestDTO);

  ProjectResponseDTO toResponse(Project project);
}
