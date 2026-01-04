package com.reactive.cinema.domain.port;

import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MoviePort {
    Mono<Movie> findById(MovieId id);

    Flux<Movie> findPage(PageRequest page);

    Mono<Long> countAll();

    Mono<Movie> create(Movie movie);
}


