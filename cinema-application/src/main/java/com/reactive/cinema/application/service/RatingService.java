package com.reactive.cinema.application.service;

import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {
    Mono<Void> rate(UserId userId, MovieId movieId, Rating rating);

    Flux<MovieCard> top10(UserId userId);
}


