package com.yahve.eventmanager.entity;

import com.yahve.eventmanager.event.EventStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "events")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "owner_id")
  private Long ownerId;

  @Column(name = "max_places", nullable = false)
  private Integer maxPlaces;

  @Column(name = "occupied_places")
  private Integer occupiedPlaces;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Column(name = "cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal cost;

  @Column(name = "duration", nullable = false)
  private Integer duration;

  @Column(name = "location_id", nullable = false)
  private Integer locationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EventStatus status;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Registration> registrations = new ArrayList<>();
}
