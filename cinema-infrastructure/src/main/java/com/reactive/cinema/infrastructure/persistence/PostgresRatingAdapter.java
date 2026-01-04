package com.reactive.cinema.infrastructure.persistence;

import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import com.reactive.cinema.domain.port.RatingPort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class PostgresRatingAdapter implements RatingPort {
    private final DatabaseClient db;

    public PostgresRatingAdapter(DatabaseClient db) {
        this.db = db;
    }

    @Override
    public Mono<Rating> getUserRating(UserId userId, MovieId movieId) {
        return db.sql("""
                        select rating
                        from user_ratings
                        where user_id = :userId and movie_id = :movieId
                        """)
                .bind("userId", userId.value())
                .bind("movieId", movieId.value())
                .map((row, meta) -> Rating.of(row.get("rating", Integer.class)))
                .one();
    }

    @Override
    public Mono<BigDecimal> getAverageRating(MovieId movieId) {
        return db.sql("""
                        SELECT COALESCE(AVG(rating)::numeric(10,2), 0) as avg_rating
                        FROM user_ratings
                        WHERE movie_id = :movieId
                        """)
                .bind("movieId", movieId.value())
                .fetch()
                .one()
                .map(row -> {
                    Object value = row.get("avg_rating");
                    return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
                })
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Mono<Void> upsertUserRating(UserId userId, MovieId movieId, Rating rating) {
        return db.sql("""
                        insert into user_ratings (user_id, movie_id, rating)
                        values (:userId, :movieId, :rating)
                        on conflict (user_id, movie_id)
                        do update set rating = excluded.rating, updated_at = now()
                        """)
                .bind("userId", userId.value())
                .bind("movieId", movieId.value())
                .bind("rating", rating.value())
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Flux<MovieId> top10MovieIdsByUser(UserId userId) {
        return db.sql("""
                        select movie_id
                        from user_ratings
                        where user_id = :userId
                        order by rating desc, updated_at desc
                        limit 10
                        """)
                .bind("userId", userId.value())
                .map((row, meta) -> MovieId.of(row.get("movie_id", UUID.class)))
                .all();
    }
}
