package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
    }

    if (authentication.getPrincipal() instanceof User user) {
      return user;
    }

    throw new AuthenticationCredentialsNotFoundException("Invalid authentication principal");
  }
}
