package com.yahve.eventmanager.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
  @NotBlank
  @Size(min = 5)
  String login,

  @NotBlank
  @Size(min = 5)
  String password,

  @NotNull(message = "Age is required")
  @Min(value = 1, message = "Age must be greater than 0")
  Integer age
) {
}
