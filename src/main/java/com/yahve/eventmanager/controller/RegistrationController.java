package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.EventDto;
import com.yahve.eventmanager.mapper.EventMapper;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {

  private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

  private final RegistrationService registrationService;
  private final EventMapper eventMapper;

  public RegistrationController(RegistrationService registrationService, EventMapper eventMapper) {
    this.registrationService = registrationService;
    this.eventMapper = eventMapper;
  }

  @PostMapping("/{eventId}")
  public ResponseEntity<Void> registerToEvent(@PathVariable Integer eventId) {
    logger.info("Received request to register for event: {}", eventId);

    registrationService.registerToEvent(eventId);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/cancel/{eventId}")
  public ResponseEntity<Void> cancelRegistration(@PathVariable Integer eventId) {
    logger.info("Received request to cancel registration for event: {}", eventId);

    registrationService.cancelRegistration(eventId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/my")
  public ResponseEntity<List<EventDto>> getUserRegistrations() {
    logger.info("Received request to get all user registrations for events");

    List<EventModel> registeredEvents = registrationService.getUserRegistrations();

    return ResponseEntity.ok(
      registeredEvents.stream().map(eventMapper::fromModelToDto).toList()
    );
  }
}
