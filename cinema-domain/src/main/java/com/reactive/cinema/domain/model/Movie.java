package com.reactive.cinema.domain.model;

import java.time.Instant;

public record Movie(
        MovieId id,
        String title,
        MovieType type,
        String description,
        Instant createdAt
) {
    public Movie {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title must not be blank");
        if (type == null) throw new IllegalArgumentException("type must not be null");
        if (description == null) description = "";
        if (createdAt == null) createdAt = Instant.EPOCH;
    }
}


