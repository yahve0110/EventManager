package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.EventDto;
import com.yahve.eventmanager.event.EventSearchFilter;
import com.yahve.eventmanager.mapper.EventMapper;
import com.yahve.eventmanager.model.EventModel;
import com.yahve.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

  private static final Logger logger = LoggerFactory.getLogger(EventController.class);

  private final EventService eventService;
  private final EventMapper eventMapper;

  public EventController(EventService eventService, EventMapper eventMapper) {
    this.eventService = eventService;
    this.eventMapper = eventMapper;
  }

  @PostMapping
  public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto eventDto) {
    logger.info("Received request to create event: {}", eventDto);

    EventModel createdEvent = eventService.createEvent(eventMapper.fromDtoToModel(eventDto));
    logger.info("Successfully created event with ID: {}", createdEvent.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.fromModelToDto(createdEvent));
  }

  @DeleteMapping("/{eventId}")
  public ResponseEntity<EventDto> deleteEvent(@PathVariable Integer eventId) {
    logger.info("Received request to delete event with ID: {}", eventId);

    EventModel eventToDelete = eventService.deleteEvent(eventId);
    logger.info("Successfully cancelled event with ID: {}", eventId);

    return ResponseEntity.status(HttpStatus.OK).body(eventMapper.fromModelToDto(eventToDelete));
  }

  @GetMapping("/{eventId}")
  public ResponseEntity<EventDto> getEvent(@PathVariable Integer eventId) {
    logger.info("Fetching event with ID: {}", eventId);

    return ResponseEntity.ok(
      eventMapper.fromModelToDto(eventService.getEventById(eventId)));
  }

  @PutMapping("/{eventId}")
  public ResponseEntity<EventDto> updateEvent(@PathVariable Integer eventId, @RequestBody EventDto eventDto) {
    logger.info("Received request to update event with ID: {} to new details: {}", eventId, eventDto);

    EventDto updatedEcentDto = eventMapper.fromModelToDto(
      eventService.updateEvent(eventId, eventMapper.fromDtoToModel(eventDto)));

    logger.info("Successfully updated event with ID: {}", eventId);
    return ResponseEntity.ok(updatedEcentDto);
  }

  @PostMapping("/search")
  public ResponseEntity<List<EventDto>> searchEvents(@RequestBody @Valid EventSearchFilter searchFilter) {
    logger.info("Get request for search events: filter={}", searchFilter);

    var foundEvents = eventService.searchByFilter(searchFilter);

    return ResponseEntity
      .status(HttpStatus.OK)
      .body(foundEvents.stream()
        .map(eventMapper::fromModelToDto)
        .toList()
      );
  }

  @GetMapping("/my")
  public ResponseEntity<List<EventDto>> getMyEvents() {
    logger.info("Fetching user events");

    List<EventModel> userEvents = eventService.getCurrentUserEvents();

    return ResponseEntity.ok(
      userEvents.stream()
        .map(eventMapper::fromModelToDto)
        .toList()
    );
  }

}
