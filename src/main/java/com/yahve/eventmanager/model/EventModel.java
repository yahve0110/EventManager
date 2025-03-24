package com.yahve.eventmanager.model;

import com.yahve.eventmanager.event.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventModel(
  Integer id,
  String name,
  Long ownerId,
  Integer maxPlaces,
  Integer occupiedPlaces,
  LocalDateTime date,
  BigDecimal cost,
  Integer duration,
  Integer locationId,
  EventStatus status
) {
}
