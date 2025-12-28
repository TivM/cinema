package com.reactive.cinema.domain.model;

import java.util.UUID;

public record MovieId(UUID value) {
    public MovieId {
        if (value == null) {
            throw new IllegalArgumentException("movieId must not be null");
        }
    }

    public static MovieId of(UUID value) {
        return new MovieId(value);
    }
}


