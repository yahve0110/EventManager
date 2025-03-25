package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.entity.Registration;
import com.yahve.eventmanager.event.EventStatus;
import com.yahve.eventmanager.event.KafkaEventMessage;
import com.yahve.eventmanager.event.KafkaEventSender;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

  private final KafkaEventSender kafkaEventSender;
  private final RegistrationRepository registrationRepository;
  private final AuthenticationService authenticationService;

  public void handleEventUpdate(Event event, EventModel updatedModel) {
    var changes = buildChanges(event, updatedModel);

    logger.info("Detected changes: {}", changes);

    if (!changes.hasAnyChange()) {
      logger.info("No changes detected, skipping Kafka notification");
      return;
    }

    logger.info("Sending Kafka update for event {}", event.getId());

    KafkaEventMessage message = new KafkaEventMessage(
      event.getId().longValue(),
      authenticationService.getAuthenticatedUser().getId(),
      event.getOwnerId(),
      changes.nameChange(),
      changes.maxPlacesChange(),
      changes.dateChange(),
      changes.costChange(),
      changes.durationChange(),
      changes.locationIdChange(),
      null,
      getSubscribers(event.getId())
    );

    kafkaEventSender.sendEvent(message);
  }

  public void sendStatusChangeNotification(Event event, EventStatus oldStatus, EventStatus newStatus) {
    if (Objects.equals(oldStatus, newStatus)) return;

    KafkaEventMessage message = new KafkaEventMessage(
      event.getId().longValue(),
      null,
      event.getOwnerId(),
      null, null, null, null, null, null,
      new KafkaEventMessage.FieldChange<>(oldStatus, newStatus),
      getSubscribers(event.getId())
    );

    kafkaEventSender.sendEvent(message);
  }

  private EventFieldChanges buildChanges(Event event, EventModel model) {
    return new EventFieldChanges(
      getFieldChange(model.name(), event.getName()),
      getFieldChange(model.maxPlaces(), event.getMaxPlaces()),
      getFieldChange(model.date(), event.getDate()),
      getFieldChange(model.cost(), event.getCost()),
      getFieldChange(model.duration(), event.getDuration()),
      getFieldChange(model.locationId(), event.getLocationId())
    );
  }

  private List<Long> getSubscribers(Integer eventId) {
    return registrationRepository.findByEventId(eventId).stream()
      .map(Registration::getUserId)
      .toList();
  }

  private <T> KafkaEventMessage.FieldChange<T> getFieldChange(T newValue, T currentValue) {
    if (newValue instanceof BigDecimal newDecimal && currentValue instanceof BigDecimal currentDecimal) {
      if (newDecimal.compareTo(currentDecimal) != 0) {
        return new KafkaEventMessage.FieldChange<>(currentValue, newValue);
      }
    } else if (!Objects.equals(newValue, currentValue)) {
      return new KafkaEventMessage.FieldChange<>(currentValue, newValue);
    }
    return null;
  }

  public record EventFieldChanges(
    KafkaEventMessage.FieldChange<String> nameChange,
    KafkaEventMessage.FieldChange<Integer> maxPlacesChange,
    KafkaEventMessage.FieldChange<java.time.LocalDateTime> dateChange,
    KafkaEventMessage.FieldChange<BigDecimal> costChange,
    KafkaEventMessage.FieldChange<Integer> durationChange,
    KafkaEventMessage.FieldChange<Integer> locationIdChange
  ) {
    public boolean hasAnyChange() {
      return nameChange != null || maxPlacesChange != null || dateChange != null ||
        costChange != null || durationChange != null || locationIdChange != null;
    }
  }
}

