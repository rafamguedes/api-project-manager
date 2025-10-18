package com.api.projects.services;

import com.api.projects.dtos.user.UserRequestDTO;
import com.api.projects.dtos.user.UserResponseDTO;
import com.api.projects.mappers.UserMapper;
import com.api.projects.repositories.UserRepository;
import com.api.projects.securities.Role;
import com.api.projects.services.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponseDTO create(UserRequestDTO request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException("Email already exists: " + request.getEmail());
    }

    var user = userMapper.toEntity(request);

    user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));

    Optional.ofNullable(request.getRole())
        .ifPresentOrElse(user::setRole, () -> user.setRole(Role.ROLE_USER));

    var savedUser = userRepository.save(user);
    return userMapper.toResponse(savedUser);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
