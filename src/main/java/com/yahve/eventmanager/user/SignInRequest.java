package com.yahve.eventmanager.user;

import jakarta.validation.constraints.NotBlank;


public record SignInRequest (
  @NotBlank
  String login,

  @NotBlank
  String password
) {
  }
