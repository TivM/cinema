package com.reactive.cinema.application.service;

import com.reactive.cinema.domain.error.NotFoundException;
import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.model.PageResult;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import com.reactive.cinema.domain.port.MoviePort;
import com.reactive.cinema.domain.port.RatingPort;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public class DefaultCatalogService implements CatalogService {
    private final MoviePort movies;
    private final RatingPort ratings;

    public DefaultCatalogService(MoviePort movies, RatingPort ratings) {
        this.movies = movies;
        this.ratings = ratings;
    }

    @Override
    public Mono<PageResult<MovieCard>> getCatalog(PageRequest page, UserId userId) {
        Mono<Long> total = movies.countAll();
        Mono<List<MovieCard>> cards = movies.findPage(page)
                .flatMap(movie ->
                        Mono.zip(
                                avgOrZero(movie.id()),
                                userId == null ? Mono.<Rating>empty() : ratings.getUserRating(userId, movie.id()).onErrorResume(e -> Mono.empty())
                        ).map(t -> new MovieCard(movie, t.getT1(), t.getT2())),
                        16
                )
                .collectList();

        return Mono.zip(cards, total)
                .map(t -> new PageResult<>(t.getT1(), page.page(), page.size(), t.getT2()));
    }

    @Override
    public Mono<MovieCard> getMovie(MovieId movieId, UserId userId) {
        return movies.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found: " + movieId.value())))
                .flatMap(movie ->
                        Mono.zip(
                                avgOrZero(movie.id()),
                                userId == null ? Mono.<Rating>empty() : ratings.getUserRating(userId, movie.id()).onErrorResume(e -> Mono.empty())
                        ).map(t -> new MovieCard(movie, t.getT1(), t.getT2()))
                );
    }

    @Override
    public Mono<PageResult<Movie>> getRawCatalog(PageRequest page) {
        Mono<Long> total = movies.countAll();
        Mono<List<Movie>> items = movies.findPage(page).collectList();
        return Mono.zip(items, total)
                .map(t -> new PageResult<>(t.getT1(), page.page(), page.size(), t.getT2()));
    }

    private Mono<BigDecimal> avgOrZero(MovieId movieId) {
        return ratings.getAverageRating(movieId).defaultIfEmpty(BigDecimal.ZERO);
    }
}


