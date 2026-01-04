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
                .map((row, meta) -> {
                    UUID movieId = row.get("id", UUID.class);
                    String title = row.get("title", String.class);
                    String typeStr = row.get("type", String.class);
                    String description = row.get("description", String.class);
                    Instant createdAt = row.get("created_at", Instant.class);
                    
                    if (movieId == null || title == null || typeStr == null) {
                        throw new IllegalStateException("Required fields cannot be null: id=" + movieId + ", title=" + title + ", type=" + typeStr);
                    }
                    
                    return new Movie(
                            MovieId.of(movieId),
                            title,
                            MovieType.valueOf(typeStr),
                            description != null ? description : "",
                            createdAt != null ? createdAt : Instant.now()
                    );
                })
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

    @Override
    public Mono<Movie> create(Movie movie) {
        return db.sql("""
                        insert into movies (id, title, type, description, created_at)
                        values (:id, :title, :type, :description, :created_at)
                        """)
                .bind("id", movie.id().value())
                .bind("title", movie.title())
                .bind("type", movie.type().name())
                .bind("description", movie.description())
                .bind("created_at", movie.createdAt())
                .fetch()
                .rowsUpdated()
                .thenReturn(movie);
    }
}


