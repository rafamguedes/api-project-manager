package com.api.projects.controllers;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

  private final UserService userService;

  @PostMapping
  @Operation(
      summary = "Create User",
      description = "Creates a new user in the system (public endpoint)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or validation error"),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - username or email already exists")
      })
  public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO request) {
    UserResponseDTO response = userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
