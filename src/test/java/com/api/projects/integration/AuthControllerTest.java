package com.api.projects.integration;

import com.api.projects.dtos.auth.LoginDTO;
import com.api.projects.dtos.auth.TokenDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseIntegration {

  private static final String LOGIN_BASE_URL = "/api/v1/auth/login";

  @Test
  @DisplayName("POST /api/v1/auth/login - Should authenticate and return token")
  void login_ShouldReturnToken_WhenValidRequest() throws Exception {
    // Arrange
    LoginDTO login = LoginDTO.builder().username("validUser").password("Aa1@valid").build();
    TokenDTO token = new TokenDTO("jwt-token");

    when(authService.authenticate(any(LoginDTO.class))).thenReturn(token);

    // Act & Assert
    mockMvc
        .perform(
            post(LOGIN_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.token", is("jwt-token")));

    verify(authService, times(1)).authenticate(any(LoginDTO.class));
  }

  @Test
  @DisplayName("POST /api/v1/auth/login - Should return 400 when validation fails")
  void login_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
    // Arrange
    LoginDTO invalid = LoginDTO.builder().username("wrongUser").password("short").build();

    // Act & Assert
    mockMvc
        .perform(
            post(LOGIN_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());

    verify(authService, never()).authenticate(any());
  }
}
