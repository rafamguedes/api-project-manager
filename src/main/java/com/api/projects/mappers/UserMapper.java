package com.api.projects.mappers;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  User toEntity(UserRequestDTO userRequestDTO);

  UserResponseDTO toResponse(User user);
}
