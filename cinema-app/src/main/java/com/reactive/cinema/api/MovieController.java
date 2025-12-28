package com.reactive.cinema.api;

import com.reactive.cinema.api.dto.MovieCardDto;
import com.reactive.cinema.api.dto.MovieDto;
import com.reactive.cinema.api.dto.PageResultDto;
import com.reactive.cinema.api.mapper.DtoMapper;
import com.reactive.cinema.application.service.CatalogService;
import com.reactive.cinema.domain.model.Movie;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.MovieCard;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.model.PageResult;
import com.reactive.cinema.domain.model.UserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MovieController {
    private final CatalogService catalog;

    public MovieController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/movies")
    public Mono<PageResultDto<MovieCardDto>> movies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID userId
    ) {
        PageRequest req = new PageRequest(page, size);
        return catalog.getCatalog(req, userId == null ? null : UserId.of(userId))
                .map(result -> new PageResult<>(
                        result.items().stream().map(DtoMapper::toDto).toList(),
                        result.page(),
                        result.size(),
                        result.total()
                ))
                .map(DtoMapper::toDto);
    }

    @GetMapping("/movies/{movieId}")
    public Mono<MovieCardDto> movie(
            @PathVariable UUID movieId,
            @RequestParam(required = false) UUID userId
    ) {
        return catalog.getMovie(MovieId.of(movieId), userId == null ? null : UserId.of(userId))
                .map(DtoMapper::toDto);
    }

    @GetMapping("/movies/raw")
    public Mono<PageResultDto<MovieDto>> rawMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return catalog.getRawCatalog(new PageRequest(page, size))
                .map(result -> new PageResult<>(
                        result.items().stream().map(DtoMapper::toDto).toList(),
                        result.page(),
                        result.size(),
                        result.total()
                ))
                .map(DtoMapper::toDto);
    }
}


