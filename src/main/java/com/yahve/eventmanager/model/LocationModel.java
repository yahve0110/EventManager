package com.yahve.eventmanager.model;

//used for service
public record LocationModel(
        Integer id,
        String name,
        String address,
        Integer capacity,
        String description
) {
}
