package com.api.projects.dtos.user;

import com.api.projects.securities.Role;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDTO {

  @NotEmpty(message = "Username must not be empty")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @NotEmpty(message = "Password must not be empty")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message =
          "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
  private String password;

  @NotEmpty(message = "Email must not be empty")
  @Email(message = "Email should be valid")
  private String email;

  @ApiModelProperty(value = "Role of the user", allowableValues = "ROLE_ADMIN,ROLE_USER")
  private Role role;
}
