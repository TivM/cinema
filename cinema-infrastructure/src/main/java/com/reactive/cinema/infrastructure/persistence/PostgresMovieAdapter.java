package com.reactive.cinema.infrastructure.persistence;

import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.MovieType;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.port.MoviePort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class PostgresMovieAdapter implements MoviePort {
    private final DatabaseClient db;

    public PostgresMovieAdapter(DatabaseClient db) {
        this.db = db;
    }

    @Override
    public Mono<Movie> findById(MovieId id) {
        return db.sql("""
                        select id, title, type, description, created_at
                        from movies
                        where id = :id
                        """)
                .bind("id", id.value())
                .map((row, meta) -> new Movie(
                        MovieId.of(row.get("id", UUID.class)),
                        row.get("title", String.class),
                        MovieType.valueOf(row.get("type", String.class)),
                        row.get("description", String.class),
                        row.get("created_at", Instant.class)
                ))
                .one();
    }

    @Override
    public Flux<Movie> findPage(PageRequest page) {
        return db.sql("""
                        select id, title, type, description, created_at
                        from movies
                        order by created_at desc
                        limit :limit offset :offset
                        """)
                .bind("limit", page.size())
                .bind("offset", page.offset())
                .map((row, meta) -> new Movie(
                        MovieId.of(row.get("id", UUID.class)),
                        row.get("title", String.class),
                        MovieType.valueOf(row.get("type", String.class)),
                        row.get("description", String.class),
                        row.get("created_at", Instant.class)
                ))
                .all();
    }

    @Override
    public Mono<Long> countAll() {
        return db.sql("select count(*) as c from movies")
                .map((row, meta) -> row.get("c", Long.class))
                .one()
                .defaultIfEmpty(0L);
    }
}


