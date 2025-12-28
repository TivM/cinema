package com.reactive.cinema.infrastructure.persistence.jpa;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RatingId implements Serializable {
    private UUID userId;
    private UUID movieId;

    public RatingId() {
    }

    public RatingId(UUID userId, UUID movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getMovieId() {
        return movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingId ratingId = (RatingId) o;
        return Objects.equals(userId, ratingId.userId) && Objects.equals(movieId, ratingId.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, movieId);
    }
}


