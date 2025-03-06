package com.yahve.eventmanager.entity;

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

  @Column(unique = true, name = "login")
  private String login;

  @Column(name = "password")
  private String password;

  @Column(name = "age")
  private Integer age;

  @Column(name = "role")
  private String role;


  public User(String login, String password, Integer age, String role) {
    this.login = login;
    this.password = password;
    this.age = age;
    this.role = role;
  }

}
