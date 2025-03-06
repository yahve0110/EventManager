package com.yahve.eventmanager.service;

import com.yahve.eventmanager.Repository.LocationRepository;
import com.yahve.eventmanager.entity.Location;
import com.yahve.eventmanager.exception.ResourceNotFoundException;
import com.yahve.eventmanager.mapper.LocationMapper;
import com.yahve.eventmanager.model.LocationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

  private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
  private final LocationMapper locationMapper;
  private final LocationRepository locationRepository;

  public LocationService(LocationMapper locationMapper, LocationRepository locationRepository) {
    this.locationMapper = locationMapper;
    this.locationRepository = locationRepository;
  }

  public LocationModel createLocation(LocationModel locationModel) {
    logger.info("Starting to create location: {}", locationModel);

    try {
      boolean locationExists = locationRepository.existsByNameAndAddress(
        locationModel.name(),
        locationModel.address()
      );

      if (locationExists) {
        throw new IllegalArgumentException("Location with the same name and address already exists");
      }

      Location locationEntity = locationMapper.fromModelToEntity(locationModel);
      logger.debug("Mapped LocationModel to Location entity: {}", locationEntity);

      Location savedLocation = locationRepository.save(locationEntity);
      logger.info("Successfully saved location with ID: {}", savedLocation.getId());

      return locationMapper.fromEntityToModel(savedLocation);
    } catch (IllegalArgumentException e) {
      logger.error("Validation error: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Error while creating location: {}", locationModel, e);
      throw new RuntimeException("Failed to create location", e);
    }
  }

  public List<LocationModel> getLocations() {
    logger.info("Fetching all locations");

    List<Location> locationEntities = locationRepository.findAll();
    logger.debug("Fetched {} locations", locationEntities.size());

    return locationMapper.fromEntitiesToModels(locationEntities);
  }

  public LocationModel getLocationById(Integer id) {
    logger.info("Fetching location with ID: {}", id);

    Location locationEntity = locationRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + id));

    return locationMapper.fromEntityToModel(locationEntity);
  }


  public void deleteLocation(Integer id) {
    logger.info("Attempting to delete location with ID: {}", id);

    Optional<Location> locationEntityOptional = locationRepository.findById(id);
    if (locationEntityOptional.isPresent()) {
      locationRepository.deleteById(id);
      logger.info("Location with ID: {} successfully deleted", id);
    } else {
      logger.warn("Location with ID: {} not found for deletion", id);
      throw new ResourceNotFoundException("Location not found with ID " + id);
    }
  }

  public LocationModel updateLocation(Integer locationId, LocationModel locationModelToUpdate) {
    logger.info("Attempting to update location with ID: {} to new details: {}", locationId, locationModelToUpdate);

    Optional<Location> locationEntityOptional = locationRepository.findById(locationId);
    if (locationEntityOptional.isPresent()) {
      Location locationEntity = locationEntityOptional.get();

      if (locationModelToUpdate.capacity() < locationEntity.getCapacity()) {
        throw new IllegalArgumentException("Cannot decrease capacity.");
      }

      locationEntity.setName(locationModelToUpdate.name());
      locationEntity.setAddress(locationModelToUpdate.address());
      locationEntity.setCapacity(locationModelToUpdate.capacity());
      locationEntity.setDescription(locationModelToUpdate.description());

      locationRepository.save(locationEntity);

      logger.info("Location with ID: {} successfully updated", locationId);

      return locationMapper.fromEntityToModel(locationEntity);
    } else {
      logger.warn("Location with ID: {} not found for update", locationId);
      throw new ResourceNotFoundException("Location not found with ID " + locationId);
    }
  }


}
