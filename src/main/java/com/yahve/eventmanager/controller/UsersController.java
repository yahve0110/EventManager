package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.UserDto;
import com.yahve.eventmanager.mapper.UserMapper;
import com.yahve.eventmanager.security.jwt.JwtTokenResponse;
import com.yahve.eventmanager.service.JwtAuthenticationService;
import com.yahve.eventmanager.user.SignInRequest;
import com.yahve.eventmanager.user.SignUpRequest;
import com.yahve.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

  private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

  private final UserService userService;
  private final JwtAuthenticationService jwtAuthenticationService;
  private final UserMapper userMapper;


  public UsersController(UserService userService, JwtAuthenticationService jwtAuthenticationService, UserMapper userMapper) {
    this.userService = userService;
    this.jwtAuthenticationService = jwtAuthenticationService;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<UserDto> registerUser(
    @RequestBody @Valid SignUpRequest signUpRequest
  ) {
    logger.info("Get request for sign-up: login={}",
      signUpRequest.login());

    var user = userService.registerUser(signUpRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    logger.info("Get request for User by id: id={}", id);
    var user = userService.getUserById(id);
    return ResponseEntity.ok(userMapper.toDto(user));
  }


  @PostMapping("/auth")
  public ResponseEntity<JwtTokenResponse> authenticate(
    @RequestBody @Valid SignInRequest signInRequest
  ) {
    logger.info("Get request for sign-in: login={})", signInRequest.login());
    var token = jwtAuthenticationService.authenticateUser(signInRequest);
    return ResponseEntity.status(HttpStatus.OK)
      .body(new JwtTokenResponse(token));
  }
}
