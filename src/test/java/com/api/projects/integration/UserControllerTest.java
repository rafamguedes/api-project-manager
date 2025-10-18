package com.api.projects.integration;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIntegration {

  private static final String USER_BASE_URL = "/api/v1/users";

  @Test
  @DisplayName("POST /api/v1/users - Should create user and return 201")
  void create_ShouldReturnCreated_WhenValidRequest() throws Exception {

    UserRequestDTO validRequest =
        UserRequestDTO.builder()
            .username("Rafael")
            .password("Aa1@strongpass")
            .email("rafael@email.com")
            .build();

    UserResponseDTO response =
        UserResponseDTO.builder().id(1L).username("Rafael").email("rafael@email.com").build();

    when(userService.create(any(UserRequestDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post(USER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.username", is("Rafael")))
        .andExpect(jsonPath("$.email", is("rafael@email.com")));

    verify(userService, times(1)).create(any());
  }

  @Test
  @DisplayName("POST /api/v1/users - Should return 400 when validation fails")
  void create_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
    UserRequestDTO invalid =
        UserRequestDTO.builder().username("").password("short").email("").build();

    mockMvc
        .perform(
            post(USER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());

    verify(userService, never()).create(any());
  }
}
