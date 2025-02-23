package com.yahve.eventmanager.model;

import com.yahve.eventmanager.user.UserRole;

public record UserModel(
  Long id,
  String login,
  Integer age,
  UserRole role
  ) {



}
