package com.reactive.cinema.infrastructure.persistence;

import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.port.MoviePort;
import com.reactive.cinema.infrastructure.persistence.jpa.MovieEntity;
import com.reactive.cinema.infrastructure.persistence.jpa.MovieRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
public class PostgresMovieAdapter implements MoviePort {
    private final MovieRepository repo;

    public PostgresMovieAdapter(MovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Movie> findById(MovieId id) {
        return Mono.fromCallable(() -> repo.findById(id.value()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.map(e -> Mono.just(toDomain(e))).orElseGet(Mono::empty));
    }

    @Override
    public Flux<Movie> findPage(PageRequest page) {
        return Mono.fromCallable(() -> repo.findAll(org.springframework.data.domain.PageRequest.of(page.page(), page.size())))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(result -> Flux.fromIterable(result.getContent()))
                .map(this::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return Mono.fromCallable(repo::count)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Movie toDomain(MovieEntity e) {
        return new Movie(
                MovieId.of(e.getId()),
                e.getTitle(),
                e.getTypeEnum(),
                e.getDescription(),
                e.getCreatedAt()
        );
    }
}


