package com.api.projects.dtos.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {

  @NotEmpty(message = "Username must not be empty")
  private String username;

  @NotEmpty(message = "Password must not be empty")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
  private String password;
}
