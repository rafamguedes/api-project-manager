package com.api.projects.unit;

import com.api.projects.dtos.auth.LoginDTO;
import com.api.projects.dtos.auth.TokenDTO;
import com.api.projects.services.AuthService;
import com.api.projects.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private AuthenticationManager authenticationManager;

  @Mock private TokenService tokenService;

  @InjectMocks private AuthService authService;

  @Test
  @DisplayName("Should return token when authentication succeeds with valid credentials")
  void authenticate_ShouldReturnToken_WhenValidCredentials() {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("Rafael").password("@Password123").build();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("Rafael");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(tokenService.generateToken("Rafael")).thenReturn("jwt-token-12345");

    // Act
    TokenDTO result = authService.authenticate(login);

    // Assert
    assertNotNull(result);
    assertEquals("jwt-token-12345", result.token());
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenService, times(1)).generateToken("Rafael");
  }

  @Test
  @DisplayName("Should create correct authentication token with username and password")
  void authenticate_ShouldCreateCorrectAuthenticationToken() {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("Rafael").password("@Password123").build();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn("Rafael");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(tokenService.generateToken("Rafael")).thenReturn("jwt-token-12345");

    // Act
    TokenDTO result = authService.authenticate(login);

    // Assert
    assertNotNull(result);
    assertEquals("jwt-token-12345", result.token());

    // Verify that the correct UsernamePasswordAuthenticationToken was created
    ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(authenticationManager).authenticate(tokenCaptor.capture());

    UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
    assertEquals("Rafael", capturedToken.getPrincipal());
    assertEquals("@Password123", capturedToken.getCredentials());
    assertTrue(capturedToken.getAuthorities().isEmpty());
  }

  @Test
  @DisplayName("Should throw BadCredentialsException when authentication fails")
  void authenticate_ShouldThrowBadCredentialsException_WhenInvalidCredentials() {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("Rafael").password("WrongPassword123").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid username or password"));

    // Act & Assert
    BadCredentialsException exception =
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(login));

    assertEquals("Invalid username or password", exception.getMessage());
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenService, never()).generateToken(anyString());
  }

  @Test
  @DisplayName("Should throw BadCredentialsException when username is empty")
  void authenticate_ShouldThrowBadCredentialsException_WhenEmptyUsername() {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("").password("@Password123").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Username cannot be empty"));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> authService.authenticate(login));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenService, never()).generateToken(anyString());
  }

  @Test
  @DisplayName("Should throw BadCredentialsException when password is empty")
  void authenticate_ShouldThrowBadCredentialsException_WhenEmptyPassword() {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("Rafael").password("").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Password cannot be empty"));

    // Act & Assert
    assertThrows(BadCredentialsException.class, () -> authService.authenticate(login));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenService, never()).generateToken(anyString());
  }

  @Test
  @DisplayName("Should throw BadCredentialsException when user does not exist")
  void authenticate_ShouldThrowBadCredentialsException_WhenUserNotFound() {
    // Arrange
    LoginDTO login =
        LoginDTO.builder().username("NonExistentUser").password("@Password123").build();

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("User not found"));

    // Act & Assert
    BadCredentialsException exception =
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(login));

    assertEquals("User not found", exception.getMessage());
    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenService, never()).generateToken(anyString());
  }

  @Test
  @DisplayName("Should throw NullPointerException when LoginDTO is null")
  void authenticate_ShouldThrowNullPointerException_WhenLoginDTOIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> authService.authenticate(null));

    verify(authenticationManager, never()).authenticate(any());
    verify(tokenService, never()).generateToken(anyString());
  }

  @Test
  @DisplayName("Should handle different usernames correctly")
  void authenticate_ShouldHandleDifferentUsernames() {
    // Arrange
    String[] usernames = {"admin", "user123", "test.user", "john_doe"};

    for (String username : usernames) {
      // Reset mocks for each iteration
      reset(authenticationManager, tokenService);

      LoginDTO login = LoginDTO.builder().username(username).password("@Password123").build();

      Authentication authentication = mock(Authentication.class);
      when(authentication.getName()).thenReturn(username);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);
      when(tokenService.generateToken(username)).thenReturn("token-for-" + username);

      // Act
      TokenDTO result = authService.authenticate(login);

      // Assert
      assertNotNull(result);
      assertEquals("token-for-" + username, result.token());
      verify(authenticationManager, times(1))
          .authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(tokenService, times(1)).generateToken(username);
    }
  }
}
