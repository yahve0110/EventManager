package com.yahve.eventmanager.mapper;

import com.yahve.eventmanager.dto.LocationDto;
import com.yahve.eventmanager.entity.Location;
import com.yahve.eventmanager.model.LocationModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {

    public LocationModel fromDtoToModel(LocationDto locationDto) {
        return  new LocationModel(
                locationDto.id(),
                locationDto.name(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description()
        );
    }

    public LocationDto fromModelToDto(LocationModel locationModel) {
        return new LocationDto(
                locationModel.id(),
                locationModel.name(),
                locationModel.address(),
                locationModel.capacity(),
                locationModel.description()
        );
    }

    public Location fromModelToEntity(LocationModel locationModel) {
        return new Location(
                locationModel.name(),
                locationModel.address(),
                locationModel.capacity(),
                locationModel.description()
        );
    }

    public LocationModel fromEntityToModel(Location locationEntity) {
        return new LocationModel(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }

    public List<LocationModel> fromEntitiesToModels(List<Location> locationEntities) {
        return locationEntities.stream()
          .map(this::fromEntityToModel)
          .collect(Collectors.toList());
    }
    public List<LocationDto> fromModelsToDtos(List<LocationModel> locationModels) {
        return locationModels.stream()
          .map(this::fromModelToDto)
          .collect(Collectors.toList());
    }

}
