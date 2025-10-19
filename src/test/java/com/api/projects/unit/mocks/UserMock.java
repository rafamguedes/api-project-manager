package com.api.projects.unit.mocks;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.entities.User;
import com.api.projects.securities.Role;

public class UserMock {

  // UserRequestDTO Mocks
  public static UserRequestDTO createValidUserRequestDTO() {
    return UserRequestDTO.builder()
        .username("testuser")
        .password("@Password123")
        .email("test@example.com")
        .build();
  }

  public static UserRequestDTO createUserRequestDTO(String username, String email) {
    return UserRequestDTO.builder()
        .username(username)
        .password("@Password123")
        .email(email)
        .build();
  }

  public static UserRequestDTO createUserRequestDTOWithExistingEmail() {
    return UserRequestDTO.builder()
        .username("existinguser")
        .password("@Password123")
        .email("exists@example.com")
        .build();
  }

  // UserResponseDTO Mocks
  public static UserResponseDTO createUserResponseDTO() {
    return UserResponseDTO.builder().id(1L).username("testuser").email("test@example.com").build();
  }

  public static UserResponseDTO createUserResponseDTO(Long id, String username, String email) {
    return UserResponseDTO.builder().id(id).username(username).email(email).build();
  }

  // User Entity Mocks
  public static User createUserEntity() {
    return User.builder()
        .id(1L)
        .username("testuser")
        .password("plain-password")
        .email("test@example.com")
        .role(Role.ROLE_USER)
        .build();
  }

  public static User createUserEntity(Long id, String username, String email) {
    return User.builder()
        .id(id)
        .username(username)
        .password("plain-password")
        .email(email)
        .role(Role.ROLE_USER)
        .build();
  }

  public static User createUserEntityWithEncryptedPassword() {
    return User.builder()
        .id(1L)
        .username("testuser")
        .password("$2a$10$encryptedPasswordHash")
        .email("test@example.com")
        .role(Role.ROLE_USER)
        .build();
  }

  public static User createUserEntityForLoadByUsername(String username) {
    return User.builder()
        .id(1L)
        .username(username)
        .password("$2a$10$encryptedPasswordHash")
        .email(username + "@example.com")
        .role(Role.ROLE_USER)
        .build();
  }

  // Bulk Data Mocks
  public static UserRequestDTO[] createMultipleUserRequests() {
    return new UserRequestDTO[] {
      createUserRequestDTO("user1", "user1@example.com"),
      createUserRequestDTO("user2", "user2@example.com"),
      createUserRequestDTO("user3", "user3@example.com")
    };
  }

  public static UserResponseDTO[] createMultipleUserResponses() {
    return new UserResponseDTO[] {
      createUserResponseDTO(1L, "user1", "user1@example.com"),
      createUserResponseDTO(2L, "user2", "user2@example.com"),
      createUserResponseDTO(3L, "user3", "user3@example.com")
    };
  }

  public static User[] createMultipleUserEntities() {
    return new User[] {
      createUserEntity(1L, "user1", "user1@example.com"),
      createUserEntity(2L, "user2", "user2@example.com"),
      createUserEntity(3L, "user3", "user3@example.com")
    };
  }

  // Adicione estes métodos ao UserMock
  public static UserRequestDTO createUserRequestDTOWithSpacesInUsername() {
    return UserRequestDTO.builder()
        .username("  normalized  user  ")
        .email("test@example.com")
        .password("@Password123")
        .build();
  }

  public static User createUserEntityWithNormalizedUsername() {
    return User.builder()
        .id(1L)
        .username("normalizeduser")
        .email("test@example.com")
        .password("$2a$10$encryptedPasswordHash")
        .role(Role.ROLE_USER)
        .build();
  }

  public static UserResponseDTO createUserResponseDTOWithNormalizedUsername() {
    return UserResponseDTO.builder()
        .id(1L)
        .username("normalizeduser")
        .email("test@example.com")
        .build();
  }
}
