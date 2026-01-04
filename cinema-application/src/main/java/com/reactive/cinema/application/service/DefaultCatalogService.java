package com.reactive.cinema.application.service;

import com.reactive.cinema.domain.error.NotFoundException;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.model.PageResult;
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
        Mono<List<MovieCard>> cards = movies.findPage(page)
                .concatMap(movie -> {
                    Mono<BigDecimal> avgMono = avgOrZero(movie.id());
                    
                    if (userId == null) {
                        return avgMono.map(avg -> new MovieCard(movie, avg, null));
                    } else {
                        return avgMono.flatMap(avg ->
                                ratings.getUserRating(userId, movie.id())
                                        .map(userRating -> new MovieCard(movie, avg, userRating))
                                        .defaultIfEmpty(new MovieCard(movie, avg, null))
                                        .onErrorResume(e -> Mono.just(new MovieCard(movie, avg, null)))
                        );
                    }
                })
                .collectList();
        
        return cards.zipWith(movies.countAll())
                .map(t -> new PageResult<>(t.getT1(), page.page(), page.size(), t.getT2()));
    }

    @Override
    public Mono<MovieCard> getMovie(MovieId movieId, UserId userId) {
        return movies.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found: " + movieId.value())))
                .flatMap(movie -> {
                    Mono<BigDecimal> avgMono = avgOrZero(movie.id());
                    
                    if (userId == null) {
                        return avgMono.map(avg -> new MovieCard(movie, avg, null));
                    } else {
                        return avgMono.flatMap(avg ->
                                ratings.getUserRating(userId, movie.id())
                                        .map(userRating -> new MovieCard(movie, avg, userRating))
                                        .defaultIfEmpty(new MovieCard(movie, avg, null))
                                        .onErrorResume(e -> Mono.just(new MovieCard(movie, avg, null)))
                        );
                    }
                });
    }

    private Mono<BigDecimal> avgOrZero(MovieId movieId) {
        return ratings.getAverageRating(movieId).defaultIfEmpty(BigDecimal.ZERO);
    }
}


