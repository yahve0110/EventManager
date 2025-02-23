package com.yahve.eventmanager.entity;

import com.yahve.eventmanager.user.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(unique = true)
  private String login;

  private String password;

  private Integer age;

  private String role;


  public User(String login, String password, Integer age, String role) {
    this.login = login;
    this.password = password;
    this.age = age;
    this.role = role;
  }

}
