package com.reactive.cinema.application.service;

import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.model.PageResult;
import com.reactive.cinema.domain.model.UserId;
import reactor.core.publisher.Mono;

public interface CatalogService {
    Mono<PageResult<MovieCard>> getCatalog(PageRequest page, UserId userId);

    Mono<MovieCard> getMovie(MovieId movieId, UserId userId);

    Mono<PageResult<Movie>> getRawCatalog(PageRequest page);
}


