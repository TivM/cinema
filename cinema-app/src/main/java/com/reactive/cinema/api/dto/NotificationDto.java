package com.reactive.cinema.api.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        String type,
        String message,
        Instant at,
        UUID userId,
        UUID movieId
) {
}


