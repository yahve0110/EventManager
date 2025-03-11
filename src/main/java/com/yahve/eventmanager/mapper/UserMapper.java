package com.yahve.eventmanager.mapper;

import com.yahve.eventmanager.dto.UserDto;
import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.model.UserModel;
import com.yahve.eventmanager.user.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto toDto(UserModel user) {
    return new UserDto(user.id(), user.login(), user.age());
  }

  public UserModel toModel(User user) {
    return new UserModel(
      user.getId(),
      user.getLogin(),
      user.getAge(),
      UserRole.valueOf(user.getRole())
    );
  }
}
