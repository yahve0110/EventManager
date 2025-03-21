package com.yahve.eventmanager.dto;

import com.yahve.eventmanager.event.EventStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDto(
  Integer id,

  @NotBlank
  String name,

  Long ownerId,

  @NotNull
  @Min(1)
  Integer maxPlaces,

  Integer occupiedPlaces,

  @NotNull
  @FutureOrPresent
  LocalDateTime date,

  @NotNull
  @Min(1)
  BigDecimal cost,

  @NotNull
  @Min(30)
  Integer duration,

  @NotNull
  @Min(1)
  Integer locationId,

  EventStatus status
) {
}