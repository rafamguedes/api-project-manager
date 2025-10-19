package com.api.projects.services;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.entities.User;
import com.api.projects.exceptions.ConflictException;
import com.api.projects.mappers.UserMapper;
import com.api.projects.repositories.UserRepository;
import com.api.projects.securities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private static final String EMAIL_ALREADY_EXISTS_MESSAGE =
      "Email already exists, please, try other email address";
  private static final String USERNAME_ALREADY_EXISTS_MESSAGE =
      "Username already exists, please, try other username";

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponseDTO create(UserRequestDTO request) {
    validateUserRules(request);

    var user =
        User.builder()
            .username(request.getUsername().trim().replaceAll("\\s+", " "))
            .email(request.getEmail())
            .password(new BCryptPasswordEncoder().encode(request.getPassword()))
            .role(request.getRole() != null ? request.getRole() : Role.ROLE_USER)
            .build();

    var savedUser = userRepository.save(user);
    return userMapper.toResponse(savedUser);
  }

  private void validateUserRules(UserRequestDTO request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ConflictException(USERNAME_ALREADY_EXISTS_MESSAGE);
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ConflictException(EMAIL_ALREADY_EXISTS_MESSAGE);
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
