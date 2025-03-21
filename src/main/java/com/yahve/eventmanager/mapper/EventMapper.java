package com.yahve.eventmanager.mapper;

import com.yahve.eventmanager.dto.EventDto;
import com.yahve.eventmanager.entity.Event;
import com.yahve.eventmanager.model.EventModel;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

  public EventModel fromDtoToModel(EventDto dto) {
    if (dto == null) {
      return null;
    }
    return new EventModel(
      dto.id(),
      dto.name(),
      dto.ownerId(),
      dto.maxPlaces(),
      dto.occupiedPlaces(),
      dto.date(),
      dto.cost(),
      dto.duration(),
      dto.locationId(),
      dto.status()
    );
  }

  public EventDto fromModelToDto(EventModel model) {
    if (model == null) {
      return null;
    }
    return new EventDto(
      model.id(),
      model.name(),
      model.ownerId(),
      model.maxPlaces(),
      model.occupiedPlaces(),
      model.date(),
      model.cost(),
      model.duration(),
      model.locationId(),
      model.status()
    );
  }

  public Event fromModelToEntity(EventModel model) {
    if (model == null) {
      return null;
    }
    Event event = new Event();
    event.setId(model.id());
    event.setName(model.name());
    event.setOwnerId(model.ownerId());
    event.setMaxPlaces(model.maxPlaces());
    event.setOccupiedPlaces(model.occupiedPlaces());
    event.setDate(model.date());
    event.setCost(model.cost());
    event.setDuration(model.duration());
    event.setLocationId(model.locationId());
    event.setStatus(model.status());
    return event;
  }

  public EventModel fromEntityToModel(Event entity) {
    if (entity == null) {
      return null;
    }
    return new EventModel(
      entity.getId(),
      entity.getName(),
      entity.getOwnerId(),
      entity.getMaxPlaces(),
      entity.getOccupiedPlaces(),
      entity.getDate(),
      entity.getCost(),
      entity.getDuration(),
      entity.getLocationId(),
      entity.getStatus()
    );
  }

}
