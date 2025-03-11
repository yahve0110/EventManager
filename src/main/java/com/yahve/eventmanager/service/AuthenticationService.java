package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.exception.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new BusinessLogicException("User is not authenticated", HttpStatus.UNAUTHORIZED);
    }

    if (authentication.getPrincipal() instanceof User user) {
      return user;
    }

    throw new BusinessLogicException("Invalid authentication principal", HttpStatus.UNAUTHORIZED);
  }
}
