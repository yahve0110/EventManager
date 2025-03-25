package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.security.jwt.JwtTokenManager;
import com.yahve.eventmanager.user.SignInRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenManager jwtTokenManager;
  private final UserService userService;

  public String authenticateUser(@Valid SignInRequest signInRequest) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        signInRequest.login(),
        signInRequest.password()
      )
    );

    User user = userService.findByLogin(signInRequest.login());

    return jwtTokenManager.generateToken(
      user.getLogin(),
      user.getId(),
      user.getRole()
    );
  }
}