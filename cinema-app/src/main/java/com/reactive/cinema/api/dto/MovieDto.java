package com.reactive.cinema.api.dto;

import java.time.Instant;
import java.util.UUID;

public record MovieDto(
        UUID id,
        String title,
        String type,
        String description,
        Instant createdAt
) {
}


