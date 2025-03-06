package com.yahve.eventmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Table(name = "location")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "capacity")
  private Integer capacity;

  @Column(name = "description")
  private String description;

  public Location(String name, String address, Integer capacity, String description) {
    this.name = name;
    this.address = address;
    this.capacity = capacity;
    this.description = description;
  }
}
