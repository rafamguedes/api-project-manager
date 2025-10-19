package com.api.projects.unit;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.entities.User;
import com.api.projects.mappers.UserMapper;
import com.api.projects.repositories.UserRepository;
import com.api.projects.securities.Role;
import com.api.projects.services.UserService;
import com.api.projects.exceptions.ConflictException; // Mudar para ConflictException
import com.api.projects.unit.mocks.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserService userService;

  @Test
  @DisplayName("Should create user when request is valid")
  void create_ShouldCreateUser_WhenValidRequest() {
    // Arrange
    UserRequestDTO request = UserMock.createValidUserRequestDTO();
    User savedUser = UserMock.createUserEntity();
    UserResponseDTO response = UserMock.createUserResponseDTO();

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toResponse(savedUser)).thenReturn(response);

    // Act
    UserResponseDTO result = userService.create(request);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("testuser", result.getUsername());
    assertEquals("test@example.com", result.getEmail());

    verify(userRepository, times(1)).existsByUsername("testuser");
    verify(userRepository, times(1)).existsByEmail("test@example.com");
    verify(userRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toResponse(savedUser);
  }

  @Test
  @DisplayName("Should encrypt password when creating user")
  void create_ShouldEncryptPassword_WhenCreatingUser() {
    // Arrange
    UserRequestDTO request = UserMock.createValidUserRequestDTO();
    User savedUser = UserMock.createUserEntityWithEncryptedPassword();
    UserResponseDTO response = UserMock.createUserResponseDTO();

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toResponse(savedUser)).thenReturn(response);

    // Act
    UserResponseDTO result = userService.create(request);

    // Assert
    assertNotNull(result);

    // Verify that password was encrypted and role was set
    verify(userRepository)
        .save(
            argThat(
                user -> {
                  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                  return user.getPassword() != null
                      && !user.getPassword().equals("@Password123")
                      && // Password should be encrypted
                      encoder.matches("@Password123", user.getPassword())
                      && // Should match original
                      user.getRole() == Role.ROLE_USER
                      && user.getUsername().equals("testuser")
                      && user.getEmail().equals("test@example.com");
                }));
  }

  @Test
  @DisplayName("Should assign ROLE_USER when creating user")
  void create_ShouldAssignUserRole_WhenCreatingUser() {
    // Arrange
    UserRequestDTO request = UserMock.createValidUserRequestDTO();
    User savedUser = UserMock.createUserEntity();
    UserResponseDTO response = UserMock.createUserResponseDTO();

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toResponse(savedUser)).thenReturn(response);

    // Act
    UserResponseDTO result = userService.create(request);

    // Assert
    assertNotNull(result);

    // Verify that role was set to ROLE_USER
    verify(userRepository).save(argThat(user -> user.getRole() == Role.ROLE_USER));
  }

  @Test
  @DisplayName("Should throw ConflictException when email already exists")
  void create_ShouldThrowConflictException_WhenEmailExists() {
    // Arrange
    UserRequestDTO request = UserMock.createUserRequestDTOWithExistingEmail();

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

    // Act & Assert
    ConflictException exception =
        assertThrows(ConflictException.class, () -> userService.create(request));

    assertEquals("Email already exists, please, try other email address", exception.getMessage());

    verify(userRepository, times(1)).existsByUsername(request.getUsername());
    verify(userRepository, times(1)).existsByEmail("exists@example.com");
    verify(userRepository, never()).save(any());
    verify(userMapper, never()).toResponse(any());
  }

  @Test
  @DisplayName("Should throw ConflictException when username already exists")
  void create_ShouldThrowConflictException_WhenUsernameExists() {
    // Arrange
    UserRequestDTO request = UserMock.createValidUserRequestDTO();

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

    // Act & Assert
    ConflictException exception =
        assertThrows(ConflictException.class, () -> userService.create(request));

    assertEquals("Username already exists, please, try other username", exception.getMessage());

    verify(userRepository, times(1)).existsByUsername("testuser");
    verify(userRepository, never()).existsByEmail(any());
    verify(userRepository, never()).save(any());
    verify(userMapper, never()).toResponse(any());
  }

  @Test
  @DisplayName("Should load user by username when user exists")
  void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
    // Arrange
    String username = "testuser";
    User user = UserMock.createUserEntityForLoadByUsername(username);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    // Act
    UserDetails userDetails = userService.loadUserByUsername(username);

    // Assert
    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
    assertEquals("$2a$10$encryptedPasswordHash", userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

    verify(userRepository, times(1)).findByUsername(username);
  }

  @Test
  @DisplayName("Should throw UsernameNotFoundException when user does not exist")
  void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
    // Arrange
    String username = "nonexistent";
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    UsernameNotFoundException exception =
        assertThrows(
            UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));

    assertEquals("nonexistent", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
  }

  @Test
  @DisplayName("Should create multiple users with different data")
  void create_ShouldHandleMultipleUsers_WithDifferentData() {
    // Arrange
    UserRequestDTO[] requests = UserMock.createMultipleUserRequests();
    UserResponseDTO[] responses = UserMock.createMultipleUserResponses();
    User[] entities = UserMock.createMultipleUserEntities();

    for (int i = 0; i < requests.length; i++) {
      // Reset mocks for each iteration
      reset(userRepository, userMapper);

      when(userRepository.existsByUsername(requests[i].getUsername())).thenReturn(false);
      when(userRepository.existsByEmail(requests[i].getEmail())).thenReturn(false);
      when(userRepository.save(any(User.class))).thenReturn(entities[i]);
      when(userMapper.toResponse(entities[i])).thenReturn(responses[i]);

      // Act
      UserResponseDTO result = userService.create(requests[i]);

      // Assert
      assertNotNull(result);
      assertEquals(responses[i].getId(), result.getId());
      assertEquals(responses[i].getUsername(), result.getUsername());
      assertEquals(responses[i].getEmail(), result.getEmail());

      verify(userRepository, times(1)).existsByUsername(requests[i].getUsername());
      verify(userRepository, times(1)).existsByEmail(requests[i].getEmail());
      verify(userRepository, times(1)).save(any(User.class));
    }
  }
}
