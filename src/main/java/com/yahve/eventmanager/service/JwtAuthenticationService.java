package com.yahve.eventmanager.service;

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

  public String authenticateUser(@Valid SignInRequest signInRequest) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        signInRequest.login(),
        signInRequest.password()
      )
    );
    return jwtTokenManager.generateToken(signInRequest.login());
  }
}
