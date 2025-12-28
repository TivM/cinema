package com.reactive.cinema.api.error;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        Instant at
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, Instant.now());
    }
}


