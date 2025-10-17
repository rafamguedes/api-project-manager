package com.api.projects.controllers;

import com.api.projects.dtos.auth.LoginDTO;
import com.api.projects.dtos.auth.TokenDTO;
import com.api.projects.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "User Login", description = "Authenticates a user and returns a JWT token")
  public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO login) {
    TokenDTO token = authService.authenticate(login);
    return ResponseEntity.ok().body(token);
  }
}
