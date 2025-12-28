package com.reactive.cinema.infrastructure.persistence;

import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import com.reactive.cinema.domain.port.RatingPort;
import com.reactive.cinema.infrastructure.persistence.jpa.RatingEntity;
import com.reactive.cinema.infrastructure.persistence.jpa.RatingId;
import com.reactive.cinema.infrastructure.persistence.jpa.RatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class PostgresRatingAdapter implements RatingPort {
    private final RatingRepository repo;

    public PostgresRatingAdapter(RatingRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Rating> getUserRating(UserId userId, MovieId movieId) {
        return Mono.fromCallable(() -> repo.findByIdUserIdAndIdMovieId(userId.value(), movieId.value()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(e -> Mono.just(Rating.of(e.getRating()))).orElseGet(Mono::empty));
    }

    @Override
    public Mono<BigDecimal> getAverageRating(MovieId movieId) {
        return Mono.fromCallable(() -> repo.averageByMovie(movieId.value()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> upsertUserRating(UserId userId, MovieId movieId, Rating rating) {
        return Mono.fromRunnable(() -> {
                    RatingId id = new RatingId(userId.value(), movieId.value());
                    RatingEntity entity = repo.findById(id).orElseGet(RatingEntity::new);
                    entity.setId(id);
                    entity.setRating(rating.value());
                    entity.setUpdatedAt(Instant.now());
                    repo.save(entity);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<MovieId> top10MovieIdsByUser(UserId userId) {
        return Mono.fromCallable(() -> repo.topByUser(userId.value(), PageRequest.of(0, 10)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ids -> Flux.fromIterable(ids).map(MovieId::of));
    }
}


