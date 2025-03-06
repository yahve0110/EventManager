package com.yahve.eventmanager.config;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.repository.UserRepository;
import com.yahve.eventmanager.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @EventListener(ContextRefreshedEvent.class)
  public void initDatabase() {
    createDefaultUser("admin", "admin", UserRole.ADMIN.name());
    createDefaultUser("user", "user", UserRole.USER.name());
  }

  private void createDefaultUser(String login, String rawPassword, String role) {
    if (!userRepository.existsByLogin(login)) {
      User user = new User(login, passwordEncoder.encode(rawPassword), 25, role);
      userRepository.save(user);
      logger.info("Default user '{}' created with role '{}'", login, role);
    } else {
      logger.warn("Default user '{}' already exists.", login);
    }
  }
}
