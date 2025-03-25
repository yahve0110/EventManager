package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.event.EventSearchFilter;
import com.yahve.eventmanager.event.EventStatus;
import com.yahve.eventmanager.exception.BusinessLogicException;
import com.yahve.eventmanager.exception.ResourceNotFoundException;
import com.yahve.eventmanager.mapper.EventMapper;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.model.LocationModel;
import com.yahve.eventmanager.repository.EventRepository;
import com.yahve.eventmanager.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
  private static final Logger logger = LoggerFactory.getLogger(EventService.class);

  private final EventRepository eventRepository;
  private final LocationService locationService;
  private final EventMapper eventMapper;
  private final NotificationService notificationService;
  private final AuthenticationService authenticationService;

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

    EventStatus oldStatus = event.getStatus();
    event.setStatus(EventStatus.CANCELLED);
    eventRepository.save(event);
    notificationService.sendStatusChangeNotification(event, oldStatus, EventStatus.CANCELLED);
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

    notificationService.handleEventUpdate(event, eventModel);

    event.setName(eventModel.name());
    event.setMaxPlaces(eventModel.maxPlaces());
    event.setDate(eventModel.date());
    event.setCost(eventModel.cost());
    event.setDuration(eventModel.duration());
    event.setLocationId(eventModel.locationId());

    eventRepository.save(event);
    logger.info("Event with ID {} has been updated", event.getId());

    return eventMapper.fromEntityToModel(event);
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
