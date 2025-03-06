package com.yahve.eventmanager.dto;

import java.time.LocalDateTime;

public record ServerErrorDto(
        String message,
        String detailedMessage,
        LocalDateTime dateTime
) { }
