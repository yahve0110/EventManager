package com.yahve.eventmanager.controller;

import com.yahve.eventmanager.dto.LocationDto;
import com.yahve.eventmanager.mapper.LocationMapper;
import com.yahve.eventmanager.model.LocationModel;
import com.yahve.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationsController {

  private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);
  private final LocationMapper locationMapper;
  private final LocationService locationService;

  public LocationsController(LocationMapper locationMapper, LocationService locationService) {
    this.locationMapper = locationMapper;
    this.locationService = locationService;
  }

  @GetMapping
  public ResponseEntity<List<LocationDto>> getLocations() {
    logger.info("Fetching all locations");

    List<LocationModel> locationModels = locationService.getLocations();
    List<LocationDto> locationDtos = locationMapper.fromModelsToDtos(locationModels);

    return ResponseEntity.ok(locationDtos);
  }

  @PostMapping
  public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationDto locationDto) {
    logger.info("Received request to create location: {}", locationDto);

    LocationModel locationModel = locationMapper.fromDtoToModel(locationDto);
    LocationModel savedLocation = locationService.createLocation(locationModel);

    LocationDto response = locationMapper.fromModelToDto(savedLocation);
    logger.info("Successfully created location with ID: {}", savedLocation.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{locationId}")
  public ResponseEntity<String> deleteLocation(@PathVariable Integer locationId) {
    logger.info("Received request to delete location with ID: {}", locationId);
    locationService.deleteLocation(locationId);
    logger.info("Successfully deleted location with ID: {}", locationId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Location deleted successfully");
  }



  @GetMapping("/{locationId}")
  public ResponseEntity<LocationDto> getLocation(@PathVariable Integer locationId) {
    logger.info("Fetching location with ID: {}", locationId);

    LocationModel locationModel = locationService.getLocationById(locationId);
    LocationDto response = locationMapper.fromModelToDto(locationModel);
    return ResponseEntity.ok(response);
  }


  @PutMapping("/{locationId}")
  public ResponseEntity<LocationDto> updateLocation(@PathVariable Integer locationId, @RequestBody LocationDto locationDto) {
    logger.info("Received request to update location with ID: {} to new details: {}", locationId, locationDto);

    LocationModel locationModelToUpdate = locationMapper.fromDtoToModel(locationDto);
    LocationModel updatedLocationModel = locationService.updateLocation(locationId, locationModelToUpdate);
    LocationDto updatedLocationDto = locationMapper.fromModelToDto(updatedLocationModel);

    logger.info("Successfully updated location with ID: {}", locationId);
    return ResponseEntity.ok(updatedLocationDto);
  }

}
