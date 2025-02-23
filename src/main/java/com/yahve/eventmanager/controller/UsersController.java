package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.UserDto;
import com.yahve.eventmanager.user.SignUpRequest;
import com.yahve.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

  private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

  private final UserService userService;

  public UsersController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserDto> registerUser(
   @RequestBody @Valid  SignUpRequest signUpRequest
  ){
  logger.info("Get request for sign-up: login={}",
    signUpRequest.login());

  var user = userService.registerUser(signUpRequest);

  return ResponseEntity.status(HttpStatus.CREATED)
    .body(new UserDto(user.id(), user.login(), user.age()));
  }
}
