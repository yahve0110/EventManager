package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.model.UserModel;
import com.yahve.eventmanager.repository.UserRepository;
import com.yahve.eventmanager.user.SignUpRequest;
import com.yahve.eventmanager.user.UserRole;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserModel registerUser(@Valid SignUpRequest signUpRequest) {
  if(userRepository.existsByLogin(signUpRequest.login())){
    throw new IllegalArgumentException("Username is already exists");
  }

    var userToSave = new User(
      signUpRequest.login(),
      signUpRequest.password(),
      signUpRequest.age(),
      UserRole.USER.name()
    );

    User savedUser = userRepository.save(userToSave);

    return new UserModel(savedUser.getId(), savedUser.getLogin(), savedUser.getAge(), UserRole.valueOf(savedUser.getRole()));
  }
}
