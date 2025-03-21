package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.LocationDto;
import com.yahve.eventmanager.mapper.LocationMapper;
import com.yahve.eventmanager.model.LocationModel;
import com.yahve.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationsController {

  private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);
  private final LocationMapper locationMapper;
  private final LocationService locationService;

  @GetMapping
  public ResponseEntity<List<LocationDto>> getLocations() {
    logger.info("Fetching all locations");

    return ResponseEntity.ok(locationMapper.fromModelsToDtos(locationService.getLocations()));
  }

  @PostMapping
  public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto locationDto) {
    logger.info("Received request to create location: {}", locationDto);

    LocationModel createdLocation = locationService.createLocation(locationMapper.fromDtoToModel(locationDto));
    logger.info("Successfully created location with ID: {}", createdLocation.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(locationMapper.fromModelToDto(createdLocation));
  }


  @DeleteMapping("/{locationId}")
  public ResponseEntity<String> deleteLocation(@PathVariable Integer locationId) {
    logger.info("Received request to delete location with ID: {}", locationId);
    locationService.deleteLocation(locationId);
    logger.info("Successfully deleted location with ID: {}", locationId);
    return ResponseEntity.noContent().build();
  }


  @GetMapping("/{locationId}")
  public ResponseEntity<LocationDto> getLocation(@PathVariable Integer locationId) {
    logger.info("Fetching location with ID: {}", locationId);

    return ResponseEntity.ok(
      locationMapper.fromModelToDto(locationService.getLocationById(locationId)));
  }


  @PutMapping("/{locationId}")
  public ResponseEntity<LocationDto> updateLocation(@PathVariable Integer locationId, @RequestBody LocationDto locationDto) {
    logger.info("Received request to update location with ID: {} to new details: {}", locationId, locationDto);

    LocationDto updatedLocationDto = locationMapper.fromModelToDto(
      locationService.updateLocation(locationId, locationMapper.fromDtoToModel(locationDto)));

    logger.info("Successfully updated location with ID: {}", locationId);
    return ResponseEntity.ok(updatedLocationDto);
  }

}
