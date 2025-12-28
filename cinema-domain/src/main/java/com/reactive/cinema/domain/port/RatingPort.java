package com.reactive.cinema.domain.port;

import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface RatingPort {
    Mono<Rating> getUserRating(UserId userId, MovieId movieId);

    Mono<BigDecimal> getAverageRating(MovieId movieId);

    Mono<Void> upsertUserRating(UserId userId, MovieId movieId, Rating rating);

    Flux<MovieId> top10MovieIdsByUser(UserId userId);
}


