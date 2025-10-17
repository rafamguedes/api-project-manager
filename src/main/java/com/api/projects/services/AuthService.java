package com.api.projects.services;

import com.api.projects.dtos.auth.LoginDTO;
import com.api.projects.dtos.auth.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  public TokenDTO authenticate(LoginDTO login) {
    var authenticationToken =
        new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

    var authentication = authenticationManager.authenticate(authenticationToken);
    var token = tokenService.generateToken(authentication.getName());

    return new TokenDTO(token);
  }
}
