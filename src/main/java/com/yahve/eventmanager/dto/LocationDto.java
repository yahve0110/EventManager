package com.yahve.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record LocationDto(
        Integer id,
        @NotBlank
        String name,
        @NotBlank
        String address,
        @NotNull @Min(1)
        Integer capacity,
        String description
) {
}
