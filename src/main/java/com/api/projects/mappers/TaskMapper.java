package com.api.projects.mappers;

import com.api.projects.dtos.task.TaskRequestDTO;
import com.api.projects.dtos.task.TaskResponseDTO;
import com.api.projects.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  Task toEntity(TaskRequestDTO taskRequestDTO);

  @Mapping(target = "project", source = "project")
  TaskResponseDTO toResponse(Task task);
}
