package com.reactive.cinema.domain.model;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }
}


