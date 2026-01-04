package com.reactive.cinema.application.service;

import com.reactive.cinema.application.notification.NotificationEvent;
import com.reactive.cinema.application.notification.NotificationHub;
import com.reactive.cinema.application.notification.NotificationType;
import com.reactive.cinema.domain.error.NotFoundException;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import com.reactive.cinema.domain.port.MoviePort;
import com.reactive.cinema.domain.port.RatingPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

public class DefaultRatingService implements RatingService {
    private final MoviePort movies;
    private final RatingPort ratings;
    private final NotificationHub notifications;

    public DefaultRatingService(MoviePort movies, RatingPort ratings, NotificationHub notifications) {
        this.movies = movies;
        this.ratings = ratings;
        this.notifications = notifications;
    }

    @Override
    public Mono<Void> rate(UserId userId, MovieId movieId, Rating rating) {
        return movies.findById(movieId)
                .switchIfEmpty(Mono.error(new NotFoundException("Movie not found: " + movieId.value())))
                .flatMap(movie -> ratings.upsertUserRating(userId, movieId, rating)
                        .doOnSuccess(ignored -> notifications.emit(new NotificationEvent(
                                NotificationType.RATING_CHANGED,
                                "Вы оценили фильм \"" + movie.title() + "\" на " + rating.value(),
                                Instant.now(),
                                userId,
                                movieId
                        )))
                );
    }

    @Override
    public Flux<MovieCard> top10(UserId userId) {
        return ratings.top10MovieIdsByUser(userId)
                .concatMap(movieId ->
                        Mono.zip(
                                movies.findById(movieId),
                                ratings.getAverageRating(movieId).defaultIfEmpty(BigDecimal.ZERO),
                                ratings.getUserRating(userId, movieId)
                        ).map(t -> new MovieCard(t.getT1(), t.getT2(), t.getT3()))
                );
    }
}


