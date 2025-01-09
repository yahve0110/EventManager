package com.yahve.eventmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String name;

  private String address;

  private Integer capacity;

  private String description;

  public Location(String name, String address, Integer capacity, String description) {
    this.name = name;
    this.address = address;
    this.capacity = capacity;
    this.description = description;
  }
}
