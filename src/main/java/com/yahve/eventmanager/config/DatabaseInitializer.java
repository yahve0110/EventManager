package com.yahve.eventmanager.config;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.service.UserService;
import com.yahve.eventmanager.user.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  public void initUsers() {
    createUserIfNotExists("admin", "admin", UserRole.ADMIN);
    createUserIfNotExists("user", "user", UserRole.USER);
  }

  private void createUserIfNotExists(String login, String password, UserRole role) {
    if (userService.userExists(login)) {
      logger.info("User '{}' already exists.", login);
      return;
    }

    var hashedPass = passwordEncoder.encode(password);
    var user = new User(login, hashedPass, 21, role.name());
    userService.saveUser(user);

    logger.info("Created default user '{}' with role '{}'", login, role);
  }
}

