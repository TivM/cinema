package com.reactive.cinema.api;

import com.reactive.cinema.api.dto.MovieCardDto;
import com.reactive.cinema.api.dto.RateRequest;
import com.reactive.cinema.api.mapper.DtoMapper;
import com.reactive.cinema.application.notification.NotificationEvent;
import com.reactive.cinema.application.notification.NotificationHub;
import com.reactive.cinema.application.notification.NotificationType;
import com.reactive.cinema.application.service.RatingService;
import com.reactive.cinema.domain.model.MovieId;
import com.reactive.cinema.domain.model.Rating;
import com.reactive.cinema.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}")
@Tag(name = "Ratings", description = "API для работы с рейтингами фильмов")
public class RatingController {
    private final RatingService ratings;
    private final NotificationHub hub;

    public RatingController(RatingService ratings, NotificationHub hub) {
        this.ratings = ratings;
        this.hub = hub;
    }

    @PutMapping("/ratings/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Оценить фильм", description = "Устанавливает или обновляет рейтинг фильма от пользователя")
    public Mono<Void> rate(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId,
            @Parameter(description = "ID фильма") @PathVariable UUID movieId,
            @RequestBody RateRequest request
    ) {
        return ratings.rate(UserId.of(userId), MovieId.of(movieId), Rating.of(request.value()));
    }

    @GetMapping("/top10")
    @Operation(summary = "Получить топ-10 фильмов", description = "Возвращает топ-10 фильмов для пользователя на основе его рейтингов")
    public Flux<MovieCardDto> top10(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId) {
        return ratings.top10(UserId.of(userId)).map(DtoMapper::toDto);
    }

    @GetMapping(value = "/top10/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Стрим топ-10 фильмов", description = "Server-Sent Events стрим, который отправляет обновления топ-10 при изменении рейтингов")
    public Flux<ServerSentEvent<List<MovieCardDto>>> top10Stream(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId) {
        UserId uid = UserId.of(userId);

        Flux<List<MovieCardDto>> updates = hub.stream()
                .filter(ev -> ev.type() == NotificationType.RATING_CHANGED && ev.userId() != null && ev.userId().equals(uid))
                .startWith(new NotificationEvent(
                        NotificationType.RATING_CHANGED,
                        "initial",
                        Instant.now(),
                        uid,
                        null
                ))
                .flatMap(ignored ->
                        ratings.top10(uid)
                                .map(DtoMapper::toDto)
                                .collectList()
                );

        return updates.map(list ->
                ServerSentEvent.builder(list)
                        .event("top10")
                        .id(Instant.now().toString())
                        .build()
        );
    }
}


