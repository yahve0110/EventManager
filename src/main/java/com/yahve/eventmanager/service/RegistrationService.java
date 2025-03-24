package com.yahve.eventmanager.service;

import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.entity.Registration;
import com.yahve.eventmanager.entity.User;
import com.yahve.eventmanager.event.EventStatus;
import com.yahve.eventmanager.exception.BusinessLogicException;
import com.yahve.eventmanager.exception.ResourceNotFoundException;
import com.yahve.eventmanager.mapper.EventMapper;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.repository.EventRepository;
import com.yahve.eventmanager.repository.RegistrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
  private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  private final RegistrationRepository registrationRepository;
  private final EventRepository eventRepository;
  private final EventMapper eventMapper;
  private final AuthenticationService authenticationService;


  @Transactional
  public void registerToEvent(Integer eventId) {
    User currentUser = authenticationService.getAuthenticatedUser();
    Long userId = currentUser.getId();

    Event event = eventRepository.findById(eventId)
      .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

    if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
      throw new BusinessLogicException("Event is already full", HttpStatus.BAD_REQUEST);
    }

    if (userId.equals(event.getOwnerId())) {
      throw new BusinessLogicException("Owner cannot register for their own event", HttpStatus.BAD_REQUEST);
    }

    if (!event.getStatus().equals(EventStatus.WAIT_START)) {
      throw new BusinessLogicException("Cannot register for event with status=" + event.getStatus(), HttpStatus.BAD_REQUEST);
    }

    boolean alreadyRegistered = registrationRepository.existsByUserIdAndEvent(userId, event);
    if (alreadyRegistered) {
      throw new BusinessLogicException("User is already registered for this event", HttpStatus.BAD_REQUEST);
    }

    registrationRepository.save(new Registration(userId, event));

    event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
    eventRepository.save(event);
  }

  @Transactional
  public void cancelRegistration(Integer eventId) {
    User currentUser = authenticationService.getAuthenticatedUser();
    Long userId = currentUser.getId();

    Event event = eventRepository.findById(eventId)
      .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

    Registration registration = registrationRepository.findByUserIdAndEvent(userId, event)
      .orElseThrow(() -> new BusinessLogicException("User is not registered for this event", HttpStatus.BAD_REQUEST));

    registrationRepository.delete(registration);

    event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
    eventRepository.save(event);

    logger.info("User {} successfully canceled registration for event {}", userId, eventId);
  }

  public List<EventModel> getUserRegistrations() {
    User currentUser = authenticationService.getAuthenticatedUser();
    Long userId = currentUser.getId();

    logger.info("Fetching registrations for user ID: {}", userId);

    List<Registration> userRegistrations = registrationRepository.findByUserId(userId);

    return userRegistrations.stream()
      .map(registration -> eventMapper.fromEntityToModel(registration.getEvent()))
      .toList();
  }
}
