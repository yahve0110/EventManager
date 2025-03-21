package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.mapper.UserMapper;
import com.yahve.eventmanager.model.UserModel;
import com.yahve.eventmanager.repository.UserRepository;
import com.yahve.eventmanager.user.SignUpRequest;
import com.yahve.eventmanager.user.UserRole;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public UserModel registerUser(@Valid SignUpRequest signUpRequest) {
    if (userExists(signUpRequest.login())) {
      throw new IllegalArgumentException("Username already exists");
    }

    var hashedPassword = passwordEncoder.encode(signUpRequest.password());
    var userToSave = new User(signUpRequest.login(), hashedPassword, signUpRequest.age(), UserRole.USER.name());
    User savedUser = userRepository.save(userToSave);

    return userMapper.toModel(savedUser);
  }

  public UserModel getUserById(Long id) {
    User userToFind = userRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

    return userMapper.toModel(userToFind);
  }

  public User findByLogin(String login) {
    return userRepository.findByLogin(login)
      .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  public boolean userExists(String login) {
    return userRepository.existsByLogin(login);
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }
}
