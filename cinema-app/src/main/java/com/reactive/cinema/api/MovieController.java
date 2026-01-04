package com.reactive.cinema.api;

import com.reactive.cinema.api.dto.MovieCardDto;
import com.reactive.cinema.api.dto.PageResultDto;
import com.reactive.cinema.api.mapper.DtoMapper;
import com.reactive.cinema.application.service.CatalogService;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.PageRequest;
import com.reactive.cinema.domain.model.PageResult;
import com.reactive.cinema.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Movies", description = "API для работы с фильмами и каталогом")
public class MovieController {
    private final CatalogService catalog;

    public MovieController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/movies")
    @Operation(summary = "Получить каталог фильмов", description = "Возвращает пагинированный список фильмов с рейтингами")
    public Mono<PageResultDto<MovieCardDto>> movies(
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "ID пользователя для персонализации рейтингов") @RequestParam(required = false) UUID userId
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
    @Operation(summary = "Получить информацию о фильме", description = "Возвращает детальную информацию о фильме по ID")
    public Mono<MovieCardDto> movie(
            @Parameter(description = "ID фильма") @PathVariable UUID movieId,
            @Parameter(description = "ID пользователя для персонализации рейтинга") @RequestParam(required = false) UUID userId
    ) {
        return catalog.getMovie(MovieId.of(movieId), userId == null ? null : UserId.of(userId))
                .map(DtoMapper::toDto);
    }

}


