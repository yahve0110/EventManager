package com.yahve.eventmanager.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEventMessage {
  private Long eventId;
  private Long changedByUserId;
  private Long ownerId;

  private FieldChange<String> name;
  private FieldChange<Integer> maxPlaces;
  private FieldChange<LocalDateTime> date;
  private FieldChange<BigDecimal> cost;
  private FieldChange<Integer> duration;
  private FieldChange<Integer> locationId;
  private FieldChange<EventStatus> status;

  private List<Long> subscribers;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class FieldChange<T> {
    private T oldValue;
    private T newValue;
  }
}
