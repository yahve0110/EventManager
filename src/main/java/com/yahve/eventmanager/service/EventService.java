package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.entity.Registration;
import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.event.EventSearchFilter;
import com.yahve.eventmanager.event.EventStatus;
import com.yahve.eventmanager.event.KafkaEventMessage;
import com.yahve.eventmanager.event.KafkaEventSender;
import com.yahve.eventmanager.exception.BusinessLogicException;
import com.yahve.eventmanager.exception.ResourceNotFoundException;
import com.yahve.eventmanager.mapper.EventMapper;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.model.LocationModel;
import com.yahve.eventmanager.repository.EventRepository;
import com.yahve.eventmanager.repository.RegistrationRepository;
import com.yahve.eventmanager.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EventService {
  private static final Logger logger = LoggerFactory.getLogger(EventService.class);

  private final EventRepository eventRepository;
  private final LocationService locationService;
  private final EventMapper eventMapper;
  private final KafkaEventSender kafkaEventSender;
  private final AuthenticationService authenticationService;
  private final RegistrationRepository registrationRepository;

  public EventModel createEvent(EventModel eventModel) {
    logger.info("Starting to create event: {}", eventModel);

    LocationModel location = locationService.getLocationById(eventModel.locationId());

    if (eventModel.maxPlaces() > location.capacity()) {
      throw new BusinessLogicException("Event max places cannot exceed location capacity", HttpStatus.BAD_REQUEST);
    }

    Long ownerId = authenticationService.getAuthenticatedUser().getId();

    EventModel completeModel = buildEventModel(eventModel, ownerId);
    Event savedEvent = eventRepository.save(eventMapper.fromModelToEntity(completeModel));

    logger.info("Successfully saved event with ID: {}", savedEvent.getId());
    return eventMapper.fromEntityToModel(savedEvent);
  }

  public EventModel deleteEvent(Integer eventId) {
    logger.info("Starting to delete event: {}", eventId);

    Event event = getEventEntityById(eventId);
    validateOwnerOrAdmin(event.getOwnerId());

    if (event.getStatus() != EventStatus.WAIT_START) {
      throw new BusinessLogicException("Cannot cancel event with status: " + event.getStatus(), HttpStatus.BAD_REQUEST);
    }

    event.setStatus(EventStatus.CANCELLED);
    eventRepository.save(event);
    logger.info("Event with ID {} has been cancelled", event.getId());

    return eventMapper.fromEntityToModel(event);
  }

  public EventModel getEventById(Integer id) {
    logger.info("Fetching event with ID: {}", id);
    return eventMapper.fromEntityToModel(getEventEntityById(id));
  }

  public EventModel updateEvent(Integer eventId, EventModel eventModel) {
    logger.info("Starting to update event: {}", eventId);

    LocationModel location = locationService.getLocationById(eventModel.locationId());
    if (eventModel.maxPlaces() > location.capacity()) {
      throw new BusinessLogicException("Event max places cannot exceed location capacity", HttpStatus.BAD_REQUEST);
    }

    Event event = getEventEntityById(eventId);
    validateOwnerOrAdmin(event.getOwnerId());

    var nameChange       = getFieldChange(event.getName(), eventModel.name(), event::setName);
    var maxPlacesChange  = getFieldChange(event.getMaxPlaces(), eventModel.maxPlaces(), event::setMaxPlaces);
    var dateChange       = getFieldChange(event.getDate(), eventModel.date(), event::setDate);
    var costChange       = getFieldChange(event.getCost(), eventModel.cost(), event::setCost);
    var durationChange   = getFieldChange(event.getDuration(), eventModel.duration(), event::setDuration);
    var locationIdChange = getFieldChange(event.getLocationId(), eventModel.locationId(), event::setLocationId);

    eventRepository.save(event);
    logger.info("Event with ID {} has been updated", event.getId());

    boolean hasChanges = nameChange != null || maxPlacesChange != null || dateChange != null ||
      costChange != null || durationChange != null || locationIdChange != null;

    if (hasChanges) {
      KafkaEventMessage kafkaEventMessage = new KafkaEventMessage(
        event.getId().longValue(),
        authenticationService.getAuthenticatedUser().getId(),
        event.getOwnerId(),
        nameChange,
        maxPlacesChange,
        dateChange,
        costChange,
        durationChange,
        locationIdChange,
        null,
        getSubscribers(event)
      );

      kafkaEventSender.sendEvent(kafkaEventMessage);
    }

    return eventMapper.fromEntityToModel(event);
  }


  private <T> KafkaEventMessage.FieldChange<T> getFieldChange(T oldValue, T newValue, Consumer<T> setter) {
    boolean changed;
    if (oldValue instanceof BigDecimal oldDecimal && newValue instanceof BigDecimal newDecimal) {
      changed = oldDecimal.compareTo(newDecimal) != 0;
    } else {
      changed = !Objects.equals(oldValue, newValue);
    }


    if (changed) {
      setter.accept(newValue);
      return new KafkaEventMessage.FieldChange<>(oldValue, newValue);
    }
    return null;
  }

  public List<Long> getSubscribers(Event event) {
    return registrationRepository.findByEventId(event.getId())
      .stream()
      .map(Registration::getUserId)
      .toList();
  }

  public List<EventModel> searchByFilter(EventSearchFilter searchFilter) {
    return eventRepository.findEvents(
        searchFilter.name(),
        searchFilter.placesMin(),
        searchFilter.placesMax(),
        searchFilter.dateStartAfter(),
        searchFilter.dateStartBefore(),
        searchFilter.costMin(),
        searchFilter.costMax(),
        searchFilter.durationMin(),
        searchFilter.durationMax(),
        searchFilter.locationId(),
        searchFilter.eventStatus())
      .stream()
      .map(eventMapper::fromEntityToModel)
      .toList();
  }

  public List<EventModel> getCurrentUserEvents() {
    Long userId = authenticationService.getAuthenticatedUser().getId();
    logger.info("Fetching events for user ID: {}", userId);

    return eventRepository.findByOwnerId(userId).stream()
      .map(eventMapper::fromEntityToModel)
      .toList();
  }

  private Event getEventEntityById(Integer eventId) {
    return eventRepository.findById(eventId)
      .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
  }

  private void validateOwnerOrAdmin(Long ownerId) {
    User user = authenticationService.getAuthenticatedUser();
    Long userId = user.getId();

    UserRole userRole;
    try {
      userRole = UserRole.valueOf(user.getRole().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BusinessLogicException("Invalid user role: " + user.getRole(), HttpStatus.FORBIDDEN);
    }

    if (!ownerId.equals(userId) && userRole != UserRole.ADMIN) {
      throw new BusinessLogicException("You do not have permission for this action", HttpStatus.FORBIDDEN);
    }
  }

  private EventModel buildEventModel(EventModel eventModel, Long ownerId) {
    return new EventModel(
      eventModel.id(),
      eventModel.name(),
      ownerId,
      eventModel.maxPlaces(),
      0,
      eventModel.date(),
      eventModel.cost(),
      eventModel.duration(),
      eventModel.locationId(),
      EventStatus.WAIT_START
    );
  }
}
