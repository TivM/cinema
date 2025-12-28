package com.reactive.cinema.domain.model;

public record Rating(int value) {
    public Rating {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("rating must be in range [1..10]");
        }
    }

    public static Rating of(int value) {
        return new Rating(value);
    }
}


